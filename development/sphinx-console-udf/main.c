#include "sphinx-console_get_snippet.c"

int main() {
    printf("Hello, World!\n");
//    printf(read_file((char*)"D:\\Work2\\sphinx-console-3-1\\development\\sphinx-console-udf\\CMakeLists.txt", ""));
//    printf("\n");
//    printf(read_file((char*)"D:\\Work2\\sphinx-console-3-1\\development\\sphinx-console-udf\\1.txt", ""));
//    printf("\n");

//    printf(get_snippet_file_path((long long)3331111l, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");
    char* path;
    path = get_snippet_file_path((long long*)1324324ll, "test_snippet_collection", "snippet_text");
    printf(path);
    printf("\n");

//    path = (char*)calloc(200, sizeof(char));
//    snprintf(path, 199, "Int - %d , unsigned int - %d , %d", (int*)0, (int*)1, (int*)1);
//    printf(path);
//    printf(read_snippet_file(path, ""));
//    printf("\n");

//    printf(get_snippet_file_path((long long)1111l, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");
//
//    printf(get_snippet_file_path((long long)1234567891l, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");
//
//    printf(get_snippet_file_path((long long)12345678911l, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");
//
//    printf(get_snippet_file_path((long long)123456789111l, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");
//
//    printf(get_snippet_file_path((long long)1234567891114l, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");
//
//    printf(get_snippet_file_path((long long)123456789123456789, "test_snippet_collection_1", "snippet_text", ""));
//    printf("\n");

    return 0;
}
