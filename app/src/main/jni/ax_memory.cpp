#include "ax_memory.h"
#include <string.h>

void* ax_alloc(size_t size) {
    void* ptr = calloc(1, size);
    return ptr;
}

void* ax_realloc(void* ptr, size_t size) {
    void* new_ptr = realloc(ptr, size);
    return new_ptr;
}

void ax_free(void** ptr) {
    if (!ptr || !*ptr) return;
    free(*ptr);
    *ptr = NULL;
}

char* ax_strdup(const char* str) {
    if (!str) return NULL;
    size_t len = strlen(str) + 1;
    char* copy = (char*)ax_alloc(len);
    if (!copy) return NULL;
    memcpy(copy, str, len);
    return copy;
}