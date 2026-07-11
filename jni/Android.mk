LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := aexon
LOCAL_MODULE_FILENAME := libaexon
LOCAL_SRC_FILES := aexon.cpp ax_loging.cpp ax_memory.cpp
LOCAL_LDFLAGS := -Wl,--gc-sections -Wl,--strip-all
LOCAL_CPPFLAGS := -Os -ffunction-sections -fdata-sections
include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)
LOCAL_MODULE := native
LOCAL_MODULE_FILENAME := libnative
LOCAL_SRC_FILES := native.cpp ax_deamon_info.cpp ax_loging.cpp ax_memory.cpp
LOCAL_LDFLAGS := -Wl,--gc-sections -Wl,--strip-all
LOCAL_CPPFLAGS := -Os -ffunction-sections -fdata-sections
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)