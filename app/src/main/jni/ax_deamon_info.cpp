#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

static const unsigned char START_FILE_ENC[] = {0x75,0x3E,0x3B,0x2E,0x3B,0x75,0x36,0x35,0x39,0x3B,0x36,0x75,0x2E,0x37,0x2A,0x75,0x74,0x3B,0x37,0x1C,0x3B,0x37,0x39,0x38};
static const unsigned char AX_KEY2 = 0x5A;

static char* ax_decode2(const unsigned char* enc, size_t len) {
    char* out = (char*)malloc(len + 1);
    if (!out) return NULL;
    for (size_t i = 0; i < len; i++) out[i] = enc[i] ^ AX_KEY2;
    out[len] = '\0';
    return out;
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_aexon_AexonMain_getStartTime(JNIEnv* env, jclass) {
    char* start_file = ax_decode2(START_FILE_ENC, sizeof(START_FILE_ENC));
    if (!start_file) return 0;

    FILE* f = fopen(start_file, "r");
    free(start_file);
    if (!f) return 0;

    long start_time = 0;
    fscanf(f, "%ld", &start_time);
    fclose(f);

    return (jlong)start_time;
}

}