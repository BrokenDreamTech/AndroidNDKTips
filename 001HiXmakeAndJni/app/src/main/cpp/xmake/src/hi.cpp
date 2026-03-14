#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_brokendream_xmake_Native_hiString(
        JNIEnv* env,
        jobject /* this */) {
    std::string hi = "Hi Xmake";
    return env->NewStringUTF(hi.c_str());
}
