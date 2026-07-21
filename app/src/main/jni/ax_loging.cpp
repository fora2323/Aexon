#include "ax_loging.h"
#include <stdio.h>
#include <string.h>

void ax_log_init() {
    FILE* f = fopen(LOG_FILE, "w");
    if (f) fclose(f);
}

void ax_log(const char* level, const char* msg) {
    char line[512];
    int len = snprintf(line, sizeof(line), "%s: %s\n", level, msg);

    FILE* f = fopen(LOG_FILE, "a");
    if (!f) return;
    fwrite(line, 1, len, f);
    fclose(f);
}