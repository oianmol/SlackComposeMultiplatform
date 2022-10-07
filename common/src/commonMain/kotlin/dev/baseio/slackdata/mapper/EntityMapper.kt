package dev.baseio.slackdata.mapper

interface EntityMapper<Domain, Data> {
  fun mapToDomain(entity: Data): Domain
  fun mapToData(model: Domain): Data
}

interface EntityToMapper<Domain1,Domain2>{
  fun mapToDomain1(entity: Domain2): Domain1
  fun mapToDomain2(model: Domain1): Domain2
}