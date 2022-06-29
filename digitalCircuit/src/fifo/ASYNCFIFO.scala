package fifo

//CDC的关键：数据的值本身，数量，顺序都不能错
//FIFO的作用:multi-Bit CDC
//FIFO设计的关键：产生可靠的FIFO读写指针和生成FIFO“空”/“满”状态标志。
//gradcode的作用：只有一位变化，用来跨时钟判断空满

import chisel3._
import chisel3.util.RegEnable

class WriteBundle(width: Int) extends Bundle{
  val clk = Input(Clock())
  val reset = Input(Bool())
  val data = Input(UInt(width.W))
  val enable = Input(Bool())
  val full = Output(Bool())
}

class ReadBundle(width: Int) extends Bundle{
  val clk = Input(Clock())
  val reset = Input(Bool())
  val data = Output(UInt(width.W))
  val enable = Input(Bool())
  val empty = Output(Bool())
}

class ASYNCFIFO(width: Int, lengthLog2: Int) extends RawModule {
  val write = IO(new WriteBundle(width))
  val read = IO(new ReadBundle(width))
  val readVaild = IO(Output(Bool()))

  def gradCode(x: UInt): UInt = {
    x ^ (x >> 1).asUInt
  }

  //  统一reset信号
  //  val sysReset = Input(Bool())
  val addrWriteNext = Wire(UInt((lengthLog2 + 1).W))
  val addrReadNext =  Wire(UInt((lengthLog2 + 1).W))

  val writeGradCode = Wire(UInt((lengthLog2 + 1).W))
  val readGradCode = Wire(UInt((lengthLog2 + 1).W))

  val ram = SyncReadMem(1 << lengthLog2, UInt(width.W)) // 2^lengthLog2
  val dataNext =  Wire(UInt(width.W))

  //write
  withClockAndReset(write.clk, write.reset) {
    val enable = write.enable && (!write.full)
    val addrWrite = RegEnable(addrWriteNext, 0.U, enable)
    val cdcRegWrite1 = RegEnable(readGradCode, 0.U, enable)
    val cdcRegWrite2 = RegEnable(cdcRegWrite1, 0.U, enable)
    val data = RegEnable(dataNext, 0.U, enable)

    writeGradCode := gradCode(addrWrite)
    write.full := (!cdcRegWrite2.head(2) === writeGradCode.head(2)) && (cdcRegWrite2(lengthLog2-1, 0) === writeGradCode(lengthLog2-1, 0))
    ram.write(addrWrite(lengthLog2,0), write.data)

    dataNext := Mux(!write.full, write.data, data)
    addrWriteNext := Mux(!write.full, addrWrite + 1.U, addrWrite)
  }

  // read
  withClockAndReset(read.clk, read.reset) {
    val enable = read.enable && (!read.empty)
    val addrRead = RegEnable(addrReadNext, 0.U, enable)
    val cdcRegRead1 = RegEnable(writeGradCode, 0.U, enable)
    val cdcRegRead2 = RegEnable(cdcRegRead1, 0.U, enable)

    readGradCode := gradCode(addrRead)
    read.empty := cdcRegRead2 === readGradCode
    read.data := ram.read(addrRead(lengthLog2,0))
    addrReadNext := Mux(!read.empty, addrRead + 1.U, addrRead)

    readVaild := enable
  }
}


