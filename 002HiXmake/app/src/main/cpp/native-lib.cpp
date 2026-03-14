#include <jni.h>
#include <string>
#include "xmake/include/add.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_brokendream_hixmake_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    int result = add(1, 2);
    std::string resultStr = std::to_string(result);
    std::string hello = "add result:" + resultStr;
    return env->NewStringUTF(hello.c_str());
}