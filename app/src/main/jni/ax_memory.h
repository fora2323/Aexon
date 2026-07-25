#ifndef AX_MEMORY_H
#define AX_MEMORY_H

#include <stdlib.h>

#define BUFFER_SM   512
#define BUFFER_MD   2048
#define BUFFER_LG   8192

void* ax_alloc(size_t size);
void* ax_realloc(void* ptr, size_t size);
void  ax_free(void** ptr);
char* ax_strdup(const char* str);

#endif