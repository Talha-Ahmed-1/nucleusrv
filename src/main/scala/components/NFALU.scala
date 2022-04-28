package nucleusrv.components
import chisel3._
import chisel3.util._

class NFALU extends Module {
  val io = IO(new Bundle {
    val input1: UInt = Input(UInt(32.W))
    val input2: UInt = Input(UInt(32.W))
   // val rmm: UInt = Input(UInt(3.W))

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

   // val xorSign = signBitIn1 ^ signBitIn2
    val andSign = signBitIn1 & signBitIn2 

    val tmp_mant = Wire(UInt(24.W))
    val tmpt_Exp = Wire(UInt(8.W))
    val diff = Wire(UInt(8.W))

    val temFrac = Wire(UInt(24.W))
    val temExp = Wire(UInt(8.W))

    val o_sign = Wire(UInt(1.W))
    val o_exp = Wire(UInt(8.W))
    val o_mant = Wire(UInt(25.W))
    
    
    tmp_mant:=0.U
    tmpt_Exp:=0.U
    diff:= 0.U
    temFrac:=0.U
    temExp:=0.U
    o_sign:=0.U
    o_exp:=0.U
    o_mant:=0.U
    
    io.result:=0.U
    when(expBitIn1===expBitIn2){
        o_exp:=expBitIn1
        when(signBitIn1===signBitIn2){
            o_sign:=andSign
            o_mant:= Cat(1.U,fracBitIn1+fracBitIn2)
        
        }.otherwise{
           when(fracBitIn1>fracBitIn2){
               o_sign:=signBitIn1
               o_mant:= Cat(0.U,fracBitIn1-fracBitIn2)
           }.otherwise{
               o_sign:=signBitIn2
               o_mant:= Cat(0.U,fracBitIn2-fracBitIn1)
           }
            
        }
    }.otherwise{
        when(expBitIn1>expBitIn2){
            o_exp:=expBitIn1
            o_sign:=signBitIn1
            diff:= expBitIn1-expBitIn2
            tmp_mant:= fracBitIn2 >>diff
            when(signBitIn1===signBitIn2){
                o_mant:= Cat(0.U,fracBitIn1)+Cat(0.U,tmp_mant)
            }.otherwise{
                o_mant:= Cat(0.U,fracBitIn1)-Cat(0.U,tmp_mant)
            }   
        }.otherwise{
            o_exp:=expBitIn2
            o_sign:=signBitIn2
            diff:= expBitIn2-expBitIn1
            tmp_mant:= fracBitIn1 >>diff
            when(signBitIn1===signBitIn2){
                o_mant:= Cat(0.U,fracBitIn2)+Cat(0.U,tmp_mant)
            }.otherwise{
                o_mant:= Cat(0.U,fracBitIn2)-Cat(0.U,tmp_mant)
            }
        }
            
        
       
        
    }
    when(o_mant(24)===1.U){
        temFrac := o_mant >> 1.U
        temExp := o_exp + 1.U
    }
    .elsewhen(o_mant(23)=/=1.U && o_exp=/=0.U){
        when (o_mant(23,3)==="b000000000000000000001".U){
            temFrac := o_mant <<20.U
            temExp := o_exp - 20.U
        }.elsewhen(o_mant(23,4)==="b00000000000000000001".U){
            temFrac := o_mant <<19.U
            temExp := o_exp - 19.U
        }.elsewhen(o_mant(23,5)==="b0000000000000000001".U){
            temFrac := o_mant <<18.U
            temExp := o_exp - 18.U
        }.elsewhen(o_mant(23,6)==="b000000000000000001".U){
            temFrac := o_mant <<17.U
            temExp := o_exp - 17.U
        }.elsewhen(o_mant(23,7)==="b00000000000000001".U){
            temFrac := o_mant <<16.U
            temExp := o_exp - 16.U
        }.elsewhen(o_mant(23,8)==="b0000000000000001".U){
            temFrac := o_mant <<15.U
            temExp := o_exp - 15.U
        }.elsewhen(o_mant(23,9)==="b000000000000001".U){
            temFrac := o_mant <<14.U
            temExp := o_exp - 14.U
        }.elsewhen(o_mant(23,10)==="b00000000000001".U){
            temFrac := o_mant <<13.U
            temExp := o_exp - 13.U
        }.elsewhen(o_mant(23,11)==="b0000000000001".U){
            temFrac := o_mant <<12.U
            temExp := o_exp - 12.U
        }.elsewhen(o_mant(23,12)==="b000000000001".U){
            temFrac := o_mant <<11.U
            temExp := o_exp - 11.U
        }.elsewhen(o_mant(23,13)==="b00000000001".U){
            temFrac := o_mant <<10.U
            temExp := o_exp - 10.U
        }.elsewhen(o_mant(23,14)==="b0000000001".U){
            temFrac := o_mant <<9.U
            temExp := o_exp - 9.U
        }.elsewhen(o_mant(23,15)==="b000000001".U){
            temFrac := o_mant <<8.U
            temExp := o_exp - 8.U
        }.elsewhen(o_mant(23,16)==="b00000001".U){
            temFrac := o_mant <<7.U
            temExp := o_exp - 7.U
        }.elsewhen(o_mant(23,17)==="b0000001".U){
            temFrac := o_mant <<6.U
            temExp := o_exp - 6.U
        }.elsewhen(o_mant(23,18)==="b000001".U){
            temFrac := o_mant <<5.U
            temExp := o_exp - 5.U
        }.elsewhen(o_mant(23,19)==="b00001".U){
            temFrac := o_mant <<4.U
            temExp := o_exp - 4.U
        }.elsewhen(o_mant(23,20)==="b0001".U){
            temFrac := o_mant <<3.U
            temExp := o_exp - 3.U
        }.elsewhen(o_mant(23,21)==="b001".U){
            temFrac := o_mant <<2.U
            temExp := o_exp - 2.U
        }.elsewhen(o_mant(23,22)==="b01".U){
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
    io.result:=Cat(o_sign,temExp,temFrac(22,0))

    // resFrac := 0.U
    // resExp := 0.U
    // temFrac := 0.U
    // temExp := 0.U
    // tmp_mant := 0.U
    

    // when(expBitIn1 === expBitIn2){
    //     when(xorSign && (fracBitIn1 === fracBitIn2)){
    //         io.result := 0.U
    //     }.elsewhen(xorSign && (fracBitIn1 > fracBitIn2)){
    //         resFrac := (Cat(0.B, fracBitIn1)) - (Cat(0.B, fracBitIn2))
    //         resExp := expBitIn1
    //         io.result := Cat(signBitIn1, temExp, temFrac(22,0))
    //     }.elsewhen(xorSign && (fracBitIn1 < fracBitIn2)){
    //         resFrac := (Cat(0.B, fracBitIn2)) - (Cat(0.B, fracBitIn1))
    //         resExp := expBitIn1
    //         io.result := Cat(signBitIn2, temExp, temFrac(22,0))
    //     }.otherwise{
    //         when(fracBitIn1 === fracBitIn2){
    //             resFrac := fracBitIn1
    //         }.otherwise{
    //             resFrac := (Cat(0.B, fracBitIn1) + Cat(0.B, fracBitIn2)) >> 1.U
    //         }
    //         resExp := expBitIn1 + 1.U
    //         io.result := Cat(andSign, resExp, resFrac(22,0))
    //     }
    // }
    // .elsewhen(expBitIn1 > expBitIn2){
    //     val diff = WireInit(expBitIn1 - expBitIn2)
    //     when(diff === 2.U){
    //         val shiftDiff = Wire(UInt(2.W))
    //         shiftDiff := fracBitIn2(1,0)
    //         dontTouch(shiftDiff)

            
    //        // val tmp_mant = Wire(UInt(23.W))
    //         tmp_mant := Cat(signBitIn2,fracBitIn2) >> diff
    //         dontTouch(tmp_mant)

    //         val o_mant = Wire(UInt(24.W))

    //         val o_man = Wire(UInt(23.W))
    //         o_man:=0.U

    //         o_mant := Cat(0.U,fracBitIn1)+ Cat(0.U,tmp_mant)

    //         when(o_mant(23)=== 1.U){
    //             o_man:= Cat(0.U,o_mant(22,0))>>1.U
    //             resExp:=expBitIn1 +1.U
    //             resFrac := o_man
    //         }.otherwise{
    //             when(signBitIn1===1.U && signBitIn2===1.U){
    //                 resFrac:= o_mant
    //                 resExp := expBitIn1
    //             }.otherwise{
    //                 o_man:= Cat((o_mant(22,21)+1.U),o_mant(20,0))
    //                 resExp := expBitIn1
    //                 resFrac := o_man
    //             }
    //         }
            
    //         //resFrac := o_man
    //         //resExp := expBitIn1
    //         io.result := Cat(andSign, resExp, resFrac(22,0))
    //     }
    //     .elsewhen(diff === 3.U){
    //         val shiftDiff = Wire(UInt(3.W))
    //         shiftDiff := fracBitIn2(2,0)
    //         dontTouch(shiftDiff)

            
    //        // val tmp_mant = Wire(UInt(23.W))
    //         tmp_mant := Cat(signBitIn2,fracBitIn2) >> diff
    //         dontTouch(tmp_mant)

    //         val o_mant = Wire(UInt(24.W))

    //         val o_man = Wire(UInt(23.W))
    //         o_man:=0.U

    //         o_mant := Cat(0.U,fracBitIn1)+ Cat(0.U,tmp_mant)

    //         when(o_mant(23)=== 1.U){
    //             o_man:= Cat(0.U,o_mant(22,0))>>1.U
    //             resExp:=expBitIn1 +1.U
    //         }.otherwise{
    //             when(signBitIn1===1.U && signBitIn2===1.U){
    //                 resFrac:= o_mant
    //                 resExp := expBitIn1
    //             }.otherwise{
    //                 o_man:= Cat((o_mant(22,20)+1.U),o_mant(19,0))
    //                 resExp := expBitIn1
    //             }
    //         }
            
    //         resFrac := o_man
    //         //resExp := expBitIn1
    //         io.result := Cat(andSign, resExp, resFrac(22,0))
    //     }
       
    //     .otherwise{
    //         io.result := 0.U
    //     }

    // }
    // .otherwise{
    //     io.result := 0.U
    // }
    


   
 

}

