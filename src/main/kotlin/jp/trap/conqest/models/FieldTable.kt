package jp.trap.conqest.models

import jp.trap.conqest.game.District
import jp.trap.conqest.game.Field
import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.roundToInt

object FieldsTable : Table() {
    val id = integer("id").index().autoIncrement()
    val center_x = integer("center_x")
    val center_y = integer("center_y")
    val center_z = integer("center_z")
    val center_world = uuid("center_world")
    val size_x = integer("size_x")
    val size_y = integer("size_y")

    override val primaryKey = PrimaryKey(id)
}

object GraphTable : Table() {
    val field_id = integer("field_id").index().references(FieldsTable.id)
    val from = integer("from")
    val to = integer("to")
}

object DistrictTable : Table() {
    val id = integer("id").index().autoIncrement()
    val field_id = integer("field_id").index().references(FieldsTable.id)
    val district_index = integer("district_index")
    val core_x = integer("core_x")
    val core_y = integer("core_y")
    val core_z = integer("core_z")
    val world = uuid("world")

    override val primaryKey = PrimaryKey(id)
}

object DistrictLocationsTable : Table() {
    val district_id = integer("district_id").index().references(DistrictTable.id)
    val x = integer("x")
    val y = integer("y")
}

private fun fieldEquals(field: Field): Op<Boolean> {
    return ((FieldsTable.size_x eq field.size.first) and (FieldsTable.size_y eq field.size.second) and (FieldsTable.center_x eq field.center.x.roundToInt()) and (FieldsTable.center_y eq field.center.y.roundToInt()) and (FieldsTable.center_z eq field.center.z.roundToInt()) and (FieldsTable.center_world eq field.center.world.uid))

}

object FieldTableUtil {
    fun saveField(field: Field) {
        val alreadyExists = transaction { FieldsTable.selectAll().where { fieldEquals(field) }.count() } > 0
        if (alreadyExists) return
        transaction {
            FieldsTable.insert {
                it[center_x] = field.center.x.roundToInt()
                it[center_y] = field.center.y.roundToInt()
                it[center_z] = field.center.z.roundToInt()
                it[center_world] = field.center.world.uid
                it[size_x] = field.size.first
                it[size_y] = field.size.second
            }
            val fieldId = FieldsTable.selectAll().where { fieldEquals(field) }.single()[FieldsTable.id]
            field.graph.forEachIndexed { fromIndex, graph ->
                graph.forEach { toIndex ->
                    GraphTable.insert {
                        it[field_id] = fieldId
                        it[from] = fromIndex
                        it[to] = toIndex
                    }
                }
            }
            field.districts.forEach { district ->
                DistrictTable.insert {
                    it[field_id] = fieldId
                    it[district_index] = district.id
                    it[core_x] = district.coreLocation.blockX
                    it[core_y] = district.coreLocation.blockY
                    it[core_z] = district.coreLocation.blockZ
                    it[world] = district.coreLocation.world.uid
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
            GraphTable.deleteWhere { field_id eq fieldId }
            val districts = DistrictTable.selectAll().where { DistrictTable.field_id eq fieldId }
            DistrictTable.deleteWhere { field_id eq fieldId }
            for (district in districts) {
                val districtId = district[DistrictTable.id]
                DistrictLocationsTable.deleteWhere { district_id eq districtId }
            }
        }
    }

    fun loadFields(): List<Field> {
        var res: List<Field> = emptyList()
        transaction {
            val fieldsRaw = FieldsTable.selectAll()
            res = fieldsRaw.map { fieldRaw ->
                val fieldId = fieldRaw[FieldsTable.id]
                val districtsRaw = DistrictTable.selectAll().where { DistrictTable.field_id eq fieldId }
                    .orderBy(DistrictTable.district_index to SortOrder.ASC)
                val districts = districtsRaw.map { districtRaw ->
                    val districtId = districtRaw[DistrictTable.id]
                    val districtLocations =
                        DistrictLocationsTable.selectAll().where { DistrictLocationsTable.district_id eq districtId }
                            .map {
                                Pair(it[DistrictLocationsTable.x], it[DistrictLocationsTable.y])
                            }.toSet()
                    val coreLocation = Location(
                        Bukkit.getWorld(districtRaw[DistrictTable.world]),
                        districtRaw[DistrictTable.core_x].toDouble(),
                        districtRaw[DistrictTable.core_y].toDouble(),
                        districtRaw[DistrictTable.core_z].toDouble()
                    )
                    District(districtRaw[DistrictTable.district_index], districtLocations, coreLocation)
                }
                val graph: MutableList<MutableSet<Int>> = mutableListOf()
                GraphTable.selectAll().where { GraphTable.field_id eq fieldId }.orderBy(GraphTable.from).forEach {
                    while (graph.size <= it[GraphTable.from]) graph.add(mutableSetOf())
                    graph[it[GraphTable.from]].add(it[GraphTable.to])
                }
                val centerLoc = Location(
                    Bukkit.getWorld(fieldRaw[FieldsTable.center_world]),
                    fieldRaw[FieldsTable.center_x].toDouble(),
                    fieldRaw[FieldsTable.center_y].toDouble(),
                    fieldRaw[FieldsTable.center_z].toDouble()
                )
                val field = Field(
                    centerLoc,
                    districts.map { district -> district.coreLocation },
                    fieldRaw[FieldsTable.size_x] to fieldRaw[FieldsTable.size_y]
                )
                field.districts = districts
                field.graph = graph
                field
            }
        }
        return res
    }
}
