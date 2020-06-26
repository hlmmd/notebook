project(PRO)
cmake_minimum_required(VERSION 2.8)
add_subdirectory(cuda ./cuda/bin)

#add_subdirectory(src ./src/bin)
#set(EXTRA_LIBS ${EXTRA_LIBS} cube print)
set(EXTRA_LIBS ${EXTRA_LIBS} cube )

SET(CMAKE_C_COMPILER g++)
list(APPEND CMAKE_C_FLAGS "-lstdc++")
 
add_executable(pro main.c)
target_link_libraries(pro ${EXTRA_LIBS})