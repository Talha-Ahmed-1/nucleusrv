package nucleusrv.components
import chisel3._
import chisel3.util._

class mul extends Module {
  val io = IO(new Bundle {
    val input1: UInt = Input(UInt(32.W))
    val input2: UInt = Input(UInt(32.W))

    val result: UInt = Output(UInt(32.W))
  })

  val signBitIn1 = WireInit(io.input1(31))    //sign bit
  val signBitIn2 = WireInit(io.input2(31))

  val expBitIn1 = Wire(UInt(8.W))             //exp bits
  expBitIn1 := io.input1(30, 23)
  val expBitIn2 = Wire(UInt(8.W))
  expBitIn2 := io.input2(30, 23)

  val fracBitIn1 = Wire(UInt(24.W))           //manteesa bits
  fracBitIn1 := Cat(1.U,io.input1(22, 0))
  val fracBitIn2 = Wire(UInt(24.W))
  fracBitIn2 := Cat(1.U,io.input2(22, 0))

  val temFrac = Wire(UInt(48.W))
  val temExp = Wire(UInt(8.W))

  val tmptFrac = Wire(UInt(25.W))
//   val tmptExp = Wire(UInt(8.W))

  val o_sign = Wire(UInt(1.W))
  val o_exp = Wire(UInt(8.W))
  val o_mant = Wire(UInt(48.W))

  val xorSign = signBitIn1 ^ signBitIn2

  temFrac:=0.U
  temExp:=0.U
  tmptFrac:=0.U
  o_sign:=0.U
  o_exp:=0.U
  o_mant:=0.U
  
  
    when(io.input1===0.U || io.input2===0.U){
        io.result:=0.U
    }.otherwise{

        o_sign:=xorSign

        o_exp := (expBitIn1+expBitIn2)-127.U

        o_mant:= fracBitIn1 * fracBitIn2

        when(o_mant(47)===1.U){
            temFrac := o_mant >> 1.U
            temExp := o_exp + 1.U
        }.elsewhen(o_mant(46)=/=1.U && o_exp=/=0.U){ 
            when(o_mant(46,41)==="b000001".U){
                temFrac := o_mant <<5.U
                temExp := o_exp - 5.U
            }.elsewhen(o_mant(46,42)==="b00001".U){
                temFrac := o_mant <<4.U
                temExp := o_exp - 4.U
            }.elsewhen(o_mant(46,43)==="b0001".U){
                temFrac := o_mant <<3.U
                temExp := o_exp - 3.U
            }.elsewhen(o_mant(46,44)==="b001".U){
                temFrac := o_mant <<2.U
                temExp := o_exp - 2.U
            }.elsewhen(o_mant(46,45)==="b01".U){
                temFrac := o_mant <<1.U
                temExp := o_exp - 1.U
            
            }.otherwise{
                temFrac := 0.U
                temExp := 0.U
            }
        
        }.otherwise{
            temFrac := o_mant
            temExp := o_exp
        }
        tmptFrac:=temFrac(46,23)
        io.result:=Cat(o_sign,temExp,tmptFrac(22,0))
        dontTouch(tmptFrac)
    }

}