add_rules("mode.debug", "mode.release")

target("hi")
    set_kind("shared")
    add_files("src/hi.cpp")
    -- 强制将目标文件输出到 build/架构名 下，从而去掉 release 目录
    set_targetdir("build/$(plat)/$(arch)")