package fi.metatavu.timebank.api.test.functional.data

import fi.metatavu.timebank.api.forecast.models.ForecastTask

/**
 * CLass for test tasks mock data
 */
class TestTasksData {

    companion object {

        /**
         * Gets list of mock ForecastTasks
         *
         * @return List of ForecastTasks
         */
        fun getForecastTasks(): List<ForecastTask> {
            return listOf(
                createTestTask(
                    id = 123,
                    title = "Basic Development",
                    nonBillable = true
                ),
                createTestTask(
                    id = 456,
                    title = "DevOps",
                    nonBillable = true
                ),
                createTestTask(
                    id = 789,
                    title = "In-House Development",
                    nonBillable = false
                )
            )
        }

        /**
         * Helper method for simplifying creating of ForecastTask objects
         *
         * @param id id
         * @param title title
         * @param nonBillable nonBillable
         * @return ForecastTask
         */
        private fun createTestTask(
            id: Int,
            title: String,
            nonBillable: Boolean
        ): ForecastTask {
            val newTask = ForecastTask()
            newTask.id = id
            newTask.title = title
            newTask.unBillable = !nonBillable

            return newTask
        }
    }
}