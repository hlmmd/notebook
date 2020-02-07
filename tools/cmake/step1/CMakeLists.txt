cmake_minimum_required (VERSION 3.3)
project (Tutorial)
# 版本号
set (Tutorial_VERSION_MAJOR 1)
set (Tutorial_VERSION_MINOR 0)
 
# 配置一个头文件，从而将CMake的一些设置传递到源文件中。
# 以TutorialConfig.h.in为模板，生成TutorialConfig.h
configure_file (
  "${PROJECT_SOURCE_DIR}/TutorialConfig.h.in"
  "${PROJECT_BINARY_DIR}/TutorialConfig.h"
  )
 
# 将构建目录添加到include的搜索路径中
# 这样就能够找到TutorialConfig.h
include_directories("${PROJECT_BINARY_DIR}")
 
# 添加可执行文件名
add_executable(Tutorial tutorial.cxx)
