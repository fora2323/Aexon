cmake_minimum_required(VERSION 3.22.1)

project("aexon_native")

# Flag optimasi ukuran (-Os), pembersihan unused symbols, dan penghapusan informasi debug (strip)
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -Os -ffunction-sections -fdata-sections")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -Os -ffunction-sections -fdata-sections")
set(CMAKE_EXE_LINKER_FLAGS_RELEASE "${CMAKE_EXE_LINKER_FLAGS_RELEASE} -Wl,--gc-sections -Wl,--strip-all")
set(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} -Wl,--gc-sections -Wl,--strip-all")

# Output binary executable dari aexon.cpp yang diberi nama libaexon.so
add_executable(aexon aexon.cpp ax_loging.cpp ax_memory.cpp)
set_target_properties(aexon PROPERTIES OUTPUT_NAME "libaexon.so")

# Target library shared native (.so)
add_library(native SHARED native.cpp ax_deamon_info.cpp ax_loging.cpp ax_memory.cpp)

# Link library log untuk modul native
find_library(log-lib log)
target_link_libraries(native ${log-lib})
0