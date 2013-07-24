package org.iisg.eca.jobs

import groovy.sql.Sql
import org.iisg.eca.domain.MaintenanceQuery

/**
 * MaintenanceJob runs in the background, running maintenance queries
 */
class MaintenanceJob {
    def dataSource

    /**
     * Set the triggers for maintenance to run
     * Currently at 8:00, 12:30 and 16:00
     */
    static triggers = {
        cron name: "maintenance0800", cronExpression: "0 0 8 ? * *"
        cron name: "maintenance1230", cronExpression: "0 30 12 ? * *"
        cron name: "maintenance1600", cronExpression: "0 0 16 ? * *"
    }
    
    // No concurrent jobs, wait until the previous one is finished
    def concurrent = false
    
    // Group name
    def group = "maintenanceGroup"
    
    /**
     * Collect the maintenance queries from the database and run them in order
     */
    def execute() {
        Sql sql = new Sql(dataSource)

         // Get all maintenance queries
        List<MaintenanceQuery> queries = MaintenanceQuery.listOrderByOrder()

        // Run each query
        for (MaintenanceQuery query : queries) {
            String[] calls = query.query.split(';')*.trim()

            for (int i=0; i<calls.length; i++) {
                if (calls[i].toLowerCase().startsWith("call")) {
                    try {
                        sql.execute(calls[i])
                    }
                    catch (Exception e) { }
                }
            }
        }
    }
}
