add_rules("mode.debug", "mode.release")

target("add")
    set_kind("shared")
    add_includedirs("include")
    add_files("src/add.cpp")
    -- 强制将目标文件输出到 build/架构名 下，从而去掉 release 目录
    set_targetdir("build/$(plat)/$(arch)")