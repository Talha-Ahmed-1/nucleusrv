
package nucleusrv.components
import chisel3._
import chisel3.util.MuxCase

class Execute extends Module {
  val io = IO(new Bundle {
    val immediate = Input(UInt(32.W))
    val readData1 = Input(UInt(32.W))
    val readData2 = Input(UInt(32.W))
    val pcAddress = Input(UInt(32.W))
    val func7 = Input(UInt(1.W))
    val func3 = Input(UInt(3.W))
    val mem_result = Input(UInt(32.W))
    val wb_result = Input(UInt(32.W))

    val readData3 = Input(UInt(32.W)) //changes
    val f5 = Input(UInt(5.W))    //change
    val opcode = Input(UInt(7.W))  //change

    val ex_mem_regWrite = Input(Bool())
    val mem_wb_regWrite = Input(Bool())
    ////
    val mem_wb_f_regWrite = Input(Bool())
    val mem_wb_f_regRead = Input(Bool())
    val ex_mem_f_regWrite = Input(Bool())
    val ex_mem_f_regRead = Input(Bool())
    ////
    val id_ex_ins = Input(UInt(32.W))
    val ex_mem_ins = Input(UInt(32.W))
    val mem_wb_ins = Input(UInt(32.W))

    val ctl_aluSrc = Input(Bool())
    val ctl_aluOp = Input(UInt(2.W))
    val ctl_aluSrc1 = Input(UInt(2.W))

    val writeData = Output(UInt(32.W))
    val ALUresult = Output(UInt(32.W))
  })

  val alu = Module(new ALU)
  val aluCtl = Module(new AluControl)
  val fu = Module(new ForwardingUnit).io

  // Forwarding Unt
  //
  fu.ex_f_regWrite := io.ex_mem_f_regWrite
  fu.mem_f_regWrite := io.mem_wb_f_regWrite
  //
  fu.ex_regWrite := io.ex_mem_regWrite
  fu.mem_regWrite := io.mem_wb_regWrite
  fu.ex_reg_rd := io.ex_mem_ins(11, 7)
  fu.mem_reg_rd := io.mem_wb_ins(11, 7)
  fu.reg_rs1 := io.id_ex_ins(19, 15)
  fu.reg_rs2 := io.id_ex_ins(24, 20)
  fu.reg_rs3 := io.id_ex_ins(31, 27)

  val inputMux1 = MuxCase(
    0.U,
    Array(
      (fu.forwardA === 0.U) -> (io.readData1),
      (fu.forwardA === 1.U) -> (io.mem_result),
      (fu.forwardA === 2.U) -> (io.wb_result)
    )
  )
  val inputMux2 = MuxCase(
    0.U,
    Array(
      (fu.forwardB === 0.U) -> (io.readData2),
      (fu.forwardB === 1.U) -> (io.mem_result),
      (fu.forwardB === 2.U) -> (io.wb_result)
    )
  )
  val inputMux3 = MuxCase(
    0.U,
    Array(
      (fu.forwardC === 0.U) -> (io.readData3),
      (fu.forwardC === 1.U) -> (io.mem_result),
      (fu.forwardC === 2.U) -> (io.wb_result)
    )
  )

  val aluIn1 = MuxCase(
    inputMux1,
    Array(
      (io.ctl_aluSrc1 === 1.U) -> io.pcAddress,
      (io.ctl_aluSrc1 === 2.U) -> 0.U
    )
  )
  val aluIn2 = Mux(io.ctl_aluSrc, inputMux2, io.immediate)
  

  aluCtl.io.f3 := io.func3
  aluCtl.io.f7 := io.func7
  aluCtl.io.aluOp := io.ctl_aluOp
  aluCtl.io.aluSrc := io.ctl_aluSrc

  alu.io.input1 := aluIn1
  alu.io.input2 := aluIn2
  alu.io.input3:= inputMux3       //changes
  alu.io.aluCtl := aluCtl.io.out
  alu.io.f5:= io.f5               //changes
  alu.io.opcode := io.opcode      //changes
  io.ALUresult := alu.io.result

  io.writeData := inputMux2
}
