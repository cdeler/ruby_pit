#include <assert.h>
#include <string.h>
#include <stdio.h>
#include <tree_sitter/api.h>

// Declare the `tree_sitter_json` function, which is
// implemented by the `tree-sitter-json` library.
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
static void init() __attribute__((constructor)) {
    parser = ts_parser_new();
    ts_parser_set_language(parser, tree_sitter_ruby());
}

static void deinit() __attribute__((destructor)) {
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


int main() {
  // Create a parser.
  TSParser *parser = ts_parser_new();

  // Set the parser's language (JSON in this case).
  ts_parser_set_language(parser, tree_sitter_json());

  // Build a syntax tree based on source code stored in a string.
  const char *source_code = "[1, null]";
  TSTree *tree = ts_parser_parse_string(
    parser,
    NULL,
    source_code,
    strlen(source_code)
  );

  // Get the root node of the syntax tree.
  TSNode root_node = ts_tree_root_node(tree);

  // Get some child nodes.
  TSNode array_node = ts_node_named_child(root_node, 0);
  TSNode number_node = ts_node_named_child(array_node, 0);

  // Check that the nodes have the expected types.
  assert(strcmp(ts_node_type(root_node), "value") == 0);
  assert(strcmp(ts_node_type(array_node), "array") == 0);
  assert(strcmp(ts_node_type(number_node), "number") == 0);

  // Check that the nodes have the expected child counts.
  assert(ts_node_child_count(root_node) == 1);
  assert(ts_node_child_count(array_node) == 5);
  assert(ts_node_named_child_count(array_node) == 2);
  assert(ts_node_child_count(number_node) == 0);

  // Print the syntax tree as an S-expression.
  char *string = ts_node_string(root_node);
  printf("Syntax tree: %s\n", string);

  // Free all of the heap-allocated memory.
  free(string);
  ts_tree_delete(tree);
  ts_parser_delete(parser);
  return 0;
}