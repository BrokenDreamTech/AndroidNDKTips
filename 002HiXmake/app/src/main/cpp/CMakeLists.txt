cmake_minimum_required(VERSION 3.22.1)

project("hixmake")

# 添加add.so
add_library(add SHARED IMPORTED)
# 设置add.so的导入规则
set_target_properties(add PROPERTIES
    IMPORTED_LOCATION
    ${CMAKE_CURRENT_SOURCE_DIR}/xmake/build/android/${ANDROID_ABI}/libadd.so
    IMPORTED_NO_SONAME TRUE
)

add_library(${CMAKE_PROJECT_NAME} SHARED
    native-lib.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
    #添加add依赖
    add
    android
    log)