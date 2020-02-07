add_library(MathFunctions mysqrt.cxx)

# 声明所有链接到我们的都需要引用当前目录，
# 去寻找MathFunctions.h，而我们自身不需要
# 我们自己需要 Tutorial_BINARY_DIR 但我们的使用者不需要，
# 所以定义为PRIVATE
  target_include_directories(MathFunctions
            INTERFACE ${CMAKE_CURRENT_SOURCE_DIR}
            PRIVATE ${Tutorial_BINARY_DIR}
            )

install(TARGETS MathFunctions DESTINATION lib)
install(FILES MathFunctions.h DESTINATION include)