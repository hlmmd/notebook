  #  首先添加一个生成表格的可执行文件
  add_executable(MakeTable MakeTable.cxx)

# 添加命令去生成源代码
add_custom_command(
  OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/Table.h
  COMMAND MakeTable ${CMAKE_CURRENT_BINARY_DIR}/Table.h
  DEPENDS MakeTable
  )

#  添加库
add_library(MathFunctions
            mysqrt.cxx
            ${CMAKE_CURRENT_BINARY_DIR}/Table.h
            )
# 声明所有链接到我们的都需要引用当前目录，
# 去寻找MathFunctions.h，而我们自身不需要
# 我们自己需要 Tutorial_BINARY_DIR 但我们的使用者不需要，
# 所以定义为PRIVATE
# 我们需要可执行目录去寻找table.h
target_include_directories(MathFunctions
          INTERFACE ${CMAKE_CURRENT_SOURCE_DIR}
          PRIVATE ${Tutorial_BINARY_DIR}
                  ${CMAKE_CURRENT_BINARY_DIR}
          )
install(TARGETS MathFunctions DESTINATION lib)
install(FILES MathFunctions.h DESTINATION include)
