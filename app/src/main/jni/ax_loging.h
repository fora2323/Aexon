#ifndef AX_LOGING_H
#define AX_LOGING_H

#define LOG_FILE "/data/data/com.aexon/files/aexon.log"

void ax_log_init();
void ax_log(const char* level, const char* msg);

#endif