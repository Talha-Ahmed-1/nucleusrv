package nucleusrv.components
import chisel3._
import chisel3.util._

class ALU extends Module {
  val io = IO(new Bundle {
    val input1: UInt = Input(UInt(32.W))
    val input2: UInt = Input(UInt(32.W))
    val input3: UInt = Input(UInt(32.W))
    val opcode: UInt = Input(UInt(7.W))
    
    val aluCtl: UInt = Input(UInt(4.W))

    val f5 : UInt = Input(UInt(5.W))

    val zero: Bool = Output(Bool())
    val result: UInt = Output(UInt(32.W))
  })
 // io.input3:=DontCare
  io.zero := DontCare
  when ((io.opcode==="b1010011".U ||io.opcode==="b1000011".U ||io.opcode==="b1000111".U ||io.opcode==="b1001011".U||io.opcode==="b1001111".U) && io.f5=/= "b11110".U){
      val fa = Module(new FALU)
      fa.io.input1:=io.input1
      fa.io.input2:=io.input2
      fa.io.input3:=io.input3
      fa.io.aluCtl:= io.f5
      fa.io.opcode:=io.opcode
      io.result:= fa.io.result
  // }.elsewhen(io.opcode==="b1000011".U){
  //   val fa = Module(new FALU)
  //     fa.io.input1:=io.input1
  //     fa.io.input2:=io.input2
  //     fa.io.input3:=io.input3
  //     fa.io.aluCtl:= io.f5
  //     fa.io.opcode:=io.opcode
  //     io.result:= fa.io.result

  }.otherwise{
  
  
      io.result := MuxCase(
        0.U,
        Array(
          (io.aluCtl === 0.U) -> (io.input1 & io.input2),
          (io.aluCtl === 1.U) -> (io.input1 | io.input2),
          (io.aluCtl === 2.U) -> (io.input1 + io.input2),
          (io.aluCtl === 3.U) -> (io.input1 - io.input2),
          (io.aluCtl === 4.U) -> (io.input1.asSInt < io.input2.asSInt).asUInt,
          (io.aluCtl === 5.U) -> (io.input1 < io.input2),
          (io.aluCtl === 6.U) -> (io.input1 << io.input2(4, 0)),
          (io.aluCtl === 7.U) -> (io.input1 >> io.input2(4, 0)),
          (io.aluCtl === 8.U) -> (io.input1.asSInt >> io.input2(4, 0)).asUInt,
          (io.aluCtl === 9.U) -> (io.input1 ^ io.input2)
        )
      )
  }
}