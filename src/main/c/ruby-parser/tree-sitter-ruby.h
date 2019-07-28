#include <jni.h>
/* Header for class cdeler_highlight_JNITokenizer */

#ifndef _Included_cdeler_highlight_JNITokenizer
#define _Included_cdeler_highlight_JNITokenizer
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cdeler_highlight_JNITokenizer
 * Method:    feed_internal
 * Signature: (Ljava/lang/String;)Lcdeler/highlight/AST;
 */
JNIEXPORT jobject JNICALL Java_cdeler_highlight_JNITokenizer_feed_1internal
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif