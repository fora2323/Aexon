#include <jni.h>
#include <sys/system_properties.h>

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_aexon_AexonMain_getVersion(JNIEnv* env, jclass) {
    char value[PROP_VALUE_MAX] = {0};
    __system_property_get("ro.build.version.release", value);
    return env->NewStringUTF(value);
}

JNIEXPORT jstring JNICALL
Java_com_aexon_AexonMain_getSdk(JNIEnv* env, jclass) {
    char value[PROP_VALUE_MAX] = {0};
    __system_property_get("ro.build.version.sdk", value);
    return env->NewStringUTF(value);
}

}