add_library(MathFunctions mysqrt.cxx)

# 声明所有链接到我们的都需要引用当前目录，
# 去寻找MathFunctions.h，而我们自身不需要
target_include_directories(MathFunctions
          INTERFACE ${CMAKE_CURRENT_SOURCE_DIR}
          )

install (TARGETS MathFunctions DESTINATION bin)
install (FILES MathFunctions.h DESTINATION include)