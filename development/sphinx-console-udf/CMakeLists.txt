cmake_minimum_required(VERSION 3.2)
project(sphinx-console-udf)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.c read_file.c)
add_executable(sphinx-console-udf ${SOURCE_FILES})