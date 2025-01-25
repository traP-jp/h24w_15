package jp.trap.conqest.models

import jp.trap.conqest.game.Field
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.roundToInt

object FieldsTable : Table() {
    val id = integer("id").index().autoIncrement()
    val center_x = integer("center_x")
    val center_y = integer("center_y")
    val size_x = integer("size_x")
    val size_y = integer("size_y")

    override val primaryKey = PrimaryKey(id)
}

object CoreLocationsTable : Table() {
    val field_id = integer("field_id").index().references(FieldsTable.id)
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    val world = uuid("world")
}

object DistrictTable : Table() {
    val id = integer("id").index().autoIncrement()
    val field_id = integer("field_id").index().references(FieldsTable.id)
    val district_index = integer("district_index")

    override val primaryKey = PrimaryKey(id)
}

object DistrictLocationsTable : Table() {
    val district_id = integer("district_id").index().references(DistrictTable.id)
    val x = integer("x")
    val y = integer("y")
}

private fun fieldEquals(field: Field): Op<Boolean> {
    return (FieldsTable.size_x eq field.size.first) and (FieldsTable.size_y eq field.size.second) and (FieldsTable.center_x eq field.center.x.roundToInt()) and (FieldsTable.center_y eq field.center.z.roundToInt())

}

object FieldTableUtil {
    fun saveField(field: Field) {
        val alreadyExists = transaction { FieldsTable.selectAll().where { fieldEquals(field) }.count() } > 0
        if (alreadyExists) return
        transaction {
            FieldsTable.insert {
                it[center_x] = field.center.x.roundToInt()
                it[center_y] = field.center.z.roundToInt()
                it[size_x] = field.size.first
                it[size_y] = field.size.second
            }
            val fieldId = FieldsTable.selectAll().where { fieldEquals(field) }.single()[FieldsTable.id]
            field.coreLocations.forEach { core ->
                CoreLocationsTable.insert {
                    it[field_id] = fieldId
                    it[x] = core.blockX
                    it[y] = core.blockY
                    it[z] = core.blockZ
                    it[world] = core.world.uid
                }
            }
            field.districts.forEach { district ->
                DistrictTable.insert {
                    it[field_id] = fieldId
                    it[district_index] = district.id
                }
                val districtId = DistrictTable.selectAll()
                    .where { (DistrictTable.field_id eq fieldId) and (DistrictTable.district_index eq district.id) }
                    .single()[DistrictTable.id]
                district.locations.forEach { location ->
                    DistrictLocationsTable.insert {
                        it[district_id] = districtId
                        it[x] = location.first
                        it[y] = location.second
                    }
                }
            }
        }
    }

    // フィールドを消すことはないため、未使用
    fun deleteField(field: Field) {
        transaction {
            val fieldId = FieldsTable.selectAll().single()[FieldsTable.id]
            FieldsTable.deleteWhere { id eq fieldId }
            CoreLocationsTable.deleteWhere { field_id eq fieldId }
            val districts = DistrictTable.selectAll().where { DistrictTable.field_id eq fieldId }
            DistrictTable.deleteWhere { field_id eq fieldId }
            for (district in districts) {
                val districtId = district[DistrictTable.id]
                DistrictLocationsTable.deleteWhere { district_id eq districtId }
            }
        }
    }
}
