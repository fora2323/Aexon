#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <time.h>
#include <sys/types.h>
#include <sys/inotify.h>
#include <fcntl.h>
#include "ax_loging.h"
#include "ax_memory.h"

#define BUFFER_SIZE 8192

static const unsigned char PID_FILE_ENC[] = {0x75,0x3E,0x3B,0x2E,0x3B,0x75,0x36,0x35,0x39,0x3B,0x36,0x75,0x2E,0x37,0x2A,0x75,0x74,0x3B,0x22,0x05,0x2A,0x28,0x35,0x39};
static const unsigned char START_FILE_ENC[] = {0x75,0x3E,0x3B,0x2E,0x3B,0x75,0x36,0x35,0x39,0x3B,0x36,0x75,0x2E,0x37,0x2A,0x75,0x74,0x3B,0x37,0x1C,0x3B,0x37,0x39,0x38};
static const unsigned char STARTER_PID_FILE_ENC[] = {0x75,0x3E,0x3B,0x2E,0x3B,0x75,0x36,0x35,0x39,0x3B,0x36,0x75,0x2E,0x37,0x2A,0x75,0x74,0x3B,0x22,0x05,0x29,0x2E,0x3B,0x28,0x2E,0x3F,0x28};
static const unsigned char AX_KEY = 0x5A;

static char* ax_decode(const unsigned char* enc, size_t len) {
    char* out = (char*)ax_alloc(len + 1);
    if (!out) return NULL;
    for (size_t i = 0; i < len; i++) out[i] = enc[i] ^ AX_KEY;
    out[len] = '\0';
    return out;
}

static void kill_old_starter() {
    char* f_path = ax_decode(STARTER_PID_FILE_ENC, sizeof(STARTER_PID_FILE_ENC));
    if (!f_path) return;

    FILE* f = fopen(f_path, "r");
    if (f) {
        int old_pid = 0;
        fscanf(f, "%d", &old_pid);
        fclose(f);
        if (old_pid > 0 && old_pid != getpid() && kill(old_pid, 0) == 0) {
            kill(old_pid, SIGKILL);
        }
    }
    ax_free((void**)&f_path);
}

static void daemonize() {
    pid_t pid = fork();
    if (pid > 0) exit(0);
    if (pid < 0) exit(1);

    setsid();

    pid = fork();
    if (pid > 0) exit(0);
    if (pid < 0) exit(1);

    signal(SIGHUP, SIG_IGN);
    chdir("/");

    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    close(STDERR_FILENO);
}

static void save_starter_pid() {
    char* f_path = ax_decode(STARTER_PID_FILE_ENC, sizeof(STARTER_PID_FILE_ENC));
    if (!f_path) return;

    FILE* f = fopen(f_path, "w");
    if (f) {
        fprintf(f, "%d", getpid());
        fclose(f);
    }
    ax_free((void**)&f_path);
}

static void format_elapsed(int seconds, char* out, size_t out_size) {
    int h = seconds / 3600;
    int m = (seconds % 3600) / 60;
    int s = seconds % 60;
    snprintf(out, out_size, "T+%02d:%02d:%02d", h, m, s);
}

static void save_start_time() {
    char* start_file = ax_decode(START_FILE_ENC, sizeof(START_FILE_ENC));
    if (!start_file) return;

    time_t now = time(NULL);
    FILE* f = fopen(start_file, "w");
    if (f) {
        fprintf(f, "%ld", (long)now);
        fclose(f);
    }
    ax_free((void**)&start_file);
}

static void kill_old_process() {
    char* pid_file = ax_decode(PID_FILE_ENC, sizeof(PID_FILE_ENC));
    if (!pid_file) return;

    FILE* f = fopen(pid_file, "r");
    if (!f) {
        ax_free((void**)&pid_file);
        return;
    }
    int old_pid = 0;
    fscanf(f, "%d", &old_pid);
    fclose(f);

    if (old_pid > 0 && old_pid != getpid()) {
        kill(old_pid, SIGKILL);
    }
    ax_free((void**)&pid_file);
}

static int is_process_alive(int pid) {
    return kill(pid, 0) == 0;
}

static int wait_for_pid(int* elapsed_out) {
    int fd = inotify_init1(IN_NONBLOCK);
    if (fd < 0) return 0;

    inotify_add_watch(fd, "/data/local/tmp", IN_CREATE | IN_CLOSE_WRITE);

    char buf[sizeof(struct inotify_event) + 256];
    struct timespec ts = {5, 0};
    fd_set fds;
    int elapsed = 0;

    for (int i = 0; i < 100; i++) {
        char* pid_file = ax_decode(PID_FILE_ENC, sizeof(PID_FILE_ENC));
        if (!pid_file) {
            close(fd);
            return 0;
        }

        FILE* f = fopen(pid_file, "r");
        ax_free((void**)&pid_file);

        if (f) {
            int pid = 0;
            fscanf(f, "%d", &pid);
            fclose(f);
            if (pid > 0) {
                close(fd);
                if (elapsed_out) *elapsed_out = elapsed;
                return pid;
            }
        }

        FD_ZERO(&fds);
        FD_SET(fd, &fds);
        pselect(fd + 1, &fds, NULL, NULL, &ts, NULL);
        read(fd, buf, sizeof(buf));

        elapsed += 5;
        char ts_str[16];
        format_elapsed(elapsed, ts_str, sizeof(ts_str));
        char msg[64];
        snprintf(msg, sizeof(msg), "waiting for server... %s", ts_str);
        ax_log("info", msg);
    }

    close(fd);
    if (elapsed_out) *elapsed_out = elapsed;
    return 0;
}

int main() {
    kill_old_starter();
    daemonize();
    save_starter_pid();

    ax_log_init();
    ax_log("info", "starter begin");

    while (1) {
        ax_log("info", "killing old process...");
        kill_old_process();

        char* pid_file = ax_decode(PID_FILE_ENC, sizeof(PID_FILE_ENC));
        if (pid_file) {
            remove(pid_file);
            ax_free((void**)&pid_file);
        }

        char* apk_path = (char*)ax_alloc(BUFFER_SIZE);
        if (!apk_path) {
            ax_log("fatal", "cannot allocate memory");
            sleep(5);
            continue;
        }

        FILE* fp = popen("pm path com.aexon | cut -d: -f2 | tr -d ' \n'", "r");
        if (!fp) {
            ax_log("fatal", "cannot get apk path");
            ax_free((void**)&apk_path);
            sleep(5);
            continue;
        }
        fgets(apk_path, BUFFER_SIZE, fp);
        pclose(fp);

        size_t len = strlen(apk_path);
        if (len > 0 && apk_path[len-1] == '\n') apk_path[len-1] = '\0';
        if (len > 0 && apk_path[len-1] == '\r') apk_path[len-1] = '\0';

        if (strlen(apk_path) == 0) {
            ax_log("fatal", "apk path is empty");
            ax_free((void**)&apk_path);
            sleep(5);
            continue;
        }

        char msg[BUFFER_SIZE];
        snprintf(msg, sizeof(msg), "apk path is %s", apk_path);
        ax_log("info", msg);
        ax_log("info", "starting server...");

        char cmd[BUFFER_SIZE];
        snprintf(cmd, sizeof(cmd), "CLASSPATH=%s /system/bin/app_process /system/bin com.aexon.starter.server.AexonServer &", apk_path);
        system(cmd);

        ax_free((void**)&apk_path);

        int elapsed = 0;
        int child_pid = wait_for_pid(&elapsed);

        if (child_pid <= 0) {
            ax_log("fatal", "server failed to start, retrying...");
            continue;
        }

        save_start_time();

        char ts_str[16];
        format_elapsed(elapsed, ts_str, sizeof(ts_str));
        snprintf(msg, sizeof(msg), "aexon_server pid is %d, took %s", child_pid, ts_str);
        ax_log("info", msg);

        while (is_process_alive(child_pid)) {
            sleep(5);
        }

        ax_log("warn", "server died, restarting...");
    }

    return 0;
}