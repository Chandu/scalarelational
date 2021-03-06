package org.scalarelational.instruction

import org.scalarelational.Session
import org.scalarelational.column.ColumnValue
import org.scalarelational.op.Condition
import org.scalarelational.table.Table


case class Update[+ResultType](table: Table,
                               values: List[ColumnValue[_, _]],
                               whereCondition: Option[Condition] = None,
                               mapResult: Int => ResultType)
  extends WhereSupport[Update[ResultType]] with Instruction[ResultType] {

  def where(condition: Condition): Update[ResultType] =
    copy(whereCondition = Option(condition))

  def result(implicit session: Session): ResultType = mapResult(table.datastore.exec(this))

  /**
   * Returns a new copy of this Update with an additional column value. Will
   * replace if the column is already represented.
   */
  def add(value: ColumnValue[_, _]): Update[ResultType] = {
    val filtered = values.filterNot(_.column == value.column)
    copy(values = value :: filtered.toList)
  }

  override def toString: String = {
    s"Update(values = ${values.mkString(", ")}, table = ${table.tableName}, where = $whereCondition)"
  }
}