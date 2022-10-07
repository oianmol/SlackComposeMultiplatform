package dev.baseio.slackdomain.mappers

interface UiModelMapper<DomainModel, UiModel> {
  fun mapToPresentation(model: DomainModel): UiModel
  fun mapToDomain(modelItem: UiModel): DomainModel
}