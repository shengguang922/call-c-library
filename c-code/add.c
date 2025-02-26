#include <stdlib.h>
#include <stdio.h>

char* add(int a, int b) {
    int result = a + b;
    // 动态分配内存来存储描述字符串
    char* description = (char*)malloc(100 * sizeof(char));
    if (description == NULL) {
        return NULL;
    }
    // 格式化字符串并存储到分配的内存中
    snprintf(description, 100, "The result of %d + %d is %d", a, b, result);
    return description;
}
