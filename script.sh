    #!/bin/bash
    # Ask the user for their name
    #echo Which Test you want to run?
    # read test_name
    #echo Running $1 ...

    

    #S_path=$PWD
    export NUCLEUSRV=$PWD
    FILE=$NUCLEUSRV/test_run_dir/Top_Test/VTop

    #export NUCLEUSRV=$SocNow_path/SoCNow/nucleusrv
    cd $NUCLEUSRV/riscv-arch-test
    make clean TARGETDIR=$NUCLEUSRV/riscv-target RISCV_TARGET=nucleusrv RISCV_DEVICE=rv32i RISCV_ISA=rv32i RISCV_TEST="I-ADD-01" TARGET_SIM=$NUCLEUSRV/test_run_dir/Top_Test/VTop
    make TARGETDIR=$NUCLEUSRV/riscv-target RISCV_TARGET=nucleusrv RISCV_DEVICE=rv32i RISCV_ISA=rv32i RISCV_TEST="I-ADD-01" TARGET_SIM=$NUCLEUSRV/test_run_dir/Top_Test/VTop | tee Test_result.txt
    cd ..
    exec bash
