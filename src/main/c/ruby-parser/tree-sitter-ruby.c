#include <assert.h>
#include <string.h>
#include <stdio.h>
#include <tree_sitter/api.h>

// Declare the `tree_sitter_ruby` function, which is
// implemented by the `tree_sitter_ruby` library.
TSLanguage *tree_sitter_ruby();

static TSParser *parser = NULL;
static TSTree *previousTree = NULL;


#ifdef _MSC_VER
#include <windows.h>

#ifdef __cplusplus
extern "C" {
#endif
BOOL WINAPI DllMain (
    HINSTANCE const instance,  // handle to DLL module
    DWORD     const reason,    // reason for calling function
    LPVOID    const reserved)  // reserved
{
    // Perform actions based on the reason for calling.
    switch (reason)
    {
    case DLL_PROCESS_ATTACH:
        parser = ts_parser_new();
        ts_parser_set_language(parser, tree_sitter_ruby());

        break;

    case DLL_THREAD_ATTACH:
        // Do thread-specific initialization.
        break;

    case DLL_THREAD_DETACH:
        // Do thread-specific cleanup.
        break;

    case DLL_PROCESS_DETACH:
        ts_parser_delete(parser);
        break;
    }
    return TRUE;  // Successful DLL_PROCESS_ATTACH.
}
#ifdef __cplusplus
};
#endif

#else
static __attribute__((constructor)) void init() {
    parser = ts_parser_new();
    ts_parser_set_language(parser, tree_sitter_ruby());
}

static __attribute__((destructor)) void deinit() {
    ts_parser_delete(parser);
}
#endif // _MSC_VER
#include "tree-sitter-ruby.h"


JNIEXPORT jobject JNICALL Java_cdeler_highlight_JNITokenizer_feed_1internal(
                    JNIEnv *env,
                    jobject thisObject,
                    jstring source_code) {
    const char *nativeString = (*env)->GetStringUTFChars(env, source_code, 0);
    TSTree *tree = ts_parser_parse_string(parser, previousTree, nativeString, strlen(nativeString));


    // Get the root node of the syntax tree.
    TSNode root_node = ts_tree_root_node(tree);

    char *string = ts_node_string(root_node);
    printf("Syntax tree: %s\n", string);

    free(string);
LE_Cleanup:
    previousTree = ts_tree_copy(tree);
    ts_tree_delete(tree);
    (*env)->ReleaseStringUTFChars(env, source_code, nativeString);
}
