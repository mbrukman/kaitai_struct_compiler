package io.kaitai.struct.format

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import io.kaitai.struct.exprlang.DataType.BaseType
import io.kaitai.struct.exprlang.{Ast, Expressions}

abstract class InstanceSpec
case class ValueInstanceSpec(value: Ast.expr, var dataType: Option[BaseType]) extends InstanceSpec
case class ParseInstanceSpec(dataType: BaseType, cond: ConditionalSpec, positionAbs: Option[Ast.expr]) extends InstanceSpec with AttrLikeSpec

object InstanceSpec {
  @JsonCreator
  def create(
              @JsonProperty("type") dataType: String,
              @JsonProperty("process") process: String,
              @JsonProperty("contents") contents: Object,
              @JsonProperty("size") _size: String,
              @JsonProperty("size-eos") sizeEos: Boolean,
              @JsonProperty("if") _ifExpr: String,
              @JsonProperty("encoding") _encoding: String,
              @JsonProperty("repeat") _repeat: String,
              @JsonProperty("repeat-expr") _repeatExpr: String,
              @JsonProperty("terminator") _terminator: String,
              @JsonProperty("consume") _consume: String,
              @JsonProperty("include") _include: String,
              @JsonProperty("eos-error") _eosError: String,
              @JsonProperty("enum") _enum: String,

              @JsonProperty("position-abs") _positionAbs: String,
              @JsonProperty("value") _value: String
            ): InstanceSpec = {
    val positionAbs = Option(_positionAbs).map(Expressions.parse)

    val value = Option(_value).map(e =>
      if (dataType != null) {
        throw new RuntimeException("instance: can't specify both 'value' and 'type'")
      } else if (process != null) {
        throw new RuntimeException("instance: can't specify both 'value' and 'process'")
      } else if (contents != null) {
        throw new RuntimeException("instance: can't specify both 'value' and 'contents'")
      } else if (_size != null) {
        throw new RuntimeException("instance: can't specify both 'value' and 'size'")
      } else if (_repeat != null) {
        throw new RuntimeException("instance: can't specify both 'value' and 'repeat'")
      } else if (_repeatExpr != null) {
        throw new RuntimeException("instance: can't specify both 'value' and 'repeat-expr'")
      } else if (positionAbs.isDefined) {
        throw new RuntimeException("instance: can't specify both 'value' and 'position-abs'")
      } else {
        Expressions.parse(e)
      }
    )

    value match {
      case None =>
        val a = AttrSpec.create(
          "fake_id",
          dataType,
          process,
          contents,
          _size,
          sizeEos,
          _ifExpr,
          _encoding,
          _repeat,
          _repeatExpr,
          _terminator,
          _consume,
          _include,
          _eosError,
          _enum
        )
        ParseInstanceSpec(a.dataType, a.cond, positionAbs)
      case Some(v) =>
        ValueInstanceSpec(v, None)
    }
  }
}