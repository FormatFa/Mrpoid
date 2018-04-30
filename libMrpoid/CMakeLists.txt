
cmake_minimum_required(VERSION 3.6)

#设置一个 Project变量保存路径这个字符

set(PROJECT src/main/jni/src)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
#--------------------mr--------------------

set(MINI_VM_VERSION 2011)
set(VM_VERSION 1986)

SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign")

#-c -littleend -O2 -DTARGET_MOD -DMTK_MOD -DMR_VERSION=${MINI_VM_VERSION} -DMR_PLAT_DRAWTEXT -DMR_PLAT_U2C
add_definitions(-c -littleend  -DTARGET_MOD -DMTK_MOD -DMR_VERSION=${MINI_VM_VERSION} -DMR_PLAT_DRAWTEXT -DMR_PLAT_U2C)



include_directories(src/main/jni/src/mr_pre/include)





#----------编译mr_vm_mini_prebuilt

include_directories(src/main/jni/src/include src/main/jni/src/mr_pre/include)

#编译mr_vm_mini_prebuilt,test.c是因为不加源文件就报错，所以写了个test.c
add_library(vm_mini_prebuilt
STATIC
${PROJECT}/mr_pre/include/test.c)

add_library(vm_full_prebuilt
SHARED
${PROJECT}/mr_pre/include/test.c)


#添加lib_vm_mini.a
add_library(mr_vm_mini
STATIC
IMPORTED)
#设置刚才,.要完整路径
set_target_properties(
mr_vm_mini
PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/${PROJECT}/mr_pre/libmr_vm_mini.a
)
target_link_libraries(vm_mini_prebuilt
 mr_vm_mini)



add_library(mr_vm_full  STATIC IMPORTED )
set_target_properties(mr_vm_full
PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/${PROJECT}/mr_pre/libmr_vm_full.a)
target_link_libraries(vm_full_prebuilt mr_vm_full)

#-------------------编译精简虚拟机-----------------
include_directories(src/main/jni/src src/main/jni/src/lib)
add_definitions(-DDSM_MINI)

add_library(mrpoid SHARED src/main/jni/src/emulator.c
	src/main/jni/src/dsm.c
	src/main/jni/src/network.c
	src/main/jni/src/utils.c
	src/main/jni/src/vm.c
	src/main/jni/src/msgqueue.c
	src/main/jni/src/register_natives.c
	src/main/jni/src/encode.c
	src/main/jni/src/font/font_sky16_2.c
	src/main/jni/src/lib/TimeUtils.c
	src/main/jni/src/lib/JniUtils.c
)









find_library(log-lib log)
target_link_libraries(mrpoid ${log-lib} )


find_library(jnigraphics-lib jnigraphics)
target_link_libraries(mrpoid ${jnigraphics-lib} )

target_link_libraries(mrpoid vm_mini_prebuilt)



#-----完整mrp虚拟机 mrpoid2
#project(mrpoid2)
include_directories(src/main/jni/src src/main/jni/src/lib)
#这里添加编译不行，怀疑打包出来的j精简版也是full
#add_definitions(-DDSM_FULL)

add_library(mrpoid2 SHARED  src/main/jni/src/emulator.c
                           	src/main/jni/src/dsm.c
                            src/main/jni/src/network.c
                           	src/main/jni/src/utils.c
                           	src/main/jni/src/vm.c
                           	src/main/jni/src/msgqueue.c
                           	src/main/jni/src/register_natives.c
                           	src/main/jni/src/encode.c
                           	src/main/jni/src/lib/TimeUtils.c
                           	src/main/jni/src/font/font_sky16_2.c
                           	src/main/jni/src/lib/JniUtils.c
)









find_library(log-lib log)
target_link_libraries(mrpoid2 ${log-lib} )


find_library(jnigraphics-lib jnigraphics)
target_link_libraries(mrpoid2 ${jnigraphics-lib} )


target_link_libraries(mrpoid2 vm_full_prebuilt)
