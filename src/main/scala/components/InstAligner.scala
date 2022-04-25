package nucleusrv.components
 
import chisel3._
import chisel3.util._

class AlignerIO extends Bundle {
    val instIn = Input(UInt(32.W))
    val instOut = Output(UInt(32.W))
    val compressed = Output(Bool())
    val half = Output(Bool())
}

class InstAligner extends Module{
    val io = IO(new AlignerIO)
    val cDecoder = Module(new CompressedDecoder).io


    val case1 = RegInit(0.B)
    val case2 = RegInit(0.B)
    val case3 = RegInit(0.B)
    val instReg = RegInit(0.U(32.W))

    cDecoder.instIn := io.instIn
    io.half := 0.B

    switch(case1){
        is (0.B){
            when(io.instIn(1,0) =/= 3.U && io.instIn(17,16) =/= 3.U){
                cDecoder.instIn := io.instIn(15,0)
                instReg := io.instIn
                case1 := true.B
            }
            .otherwise{
                cDecoder.instIn := io.instIn
            }
        }
        is (1.B){
            cDecoder.instIn := instReg(31,16)
            case1 := false.B
        }
    }

    switch(case2){
        is (0.B){
            when(io.instIn(17,16) === 3.U && io.instIn(1,0) =/= 3.U && case3 === 0.B){
                cDecoder.instIn := io.instIn(15,0)
                instReg := io.instIn
                case2 := true.B
                case3 := true.B
            }
            .elsewhen(instReg(17,16) =/= 3.U && case3 === 1.B){
                // cDecoder.instIn := io.instIn(31,16)
                case1 := false.B
                case3 := false.B
            }
        }
        is (1.B){
            cDecoder.instIn := Cat(io.instIn(15,0), instReg(31,16))
            io.half := 1.B
            when (io.instIn(17,16) =/= 3.U){
                instReg := io.instIn
                case1 := true.B
                case2 := false.B
            }.elsewhen(io.instIn(17,16) === 3.U){
                instReg := io.instIn
                case2 := true.B
            }.otherwise{
                case2 := false.B
            }
        }
    }
    

    io.compressed := cDecoder.compressed
    io.instOut := cDecoder.instOut

    
}