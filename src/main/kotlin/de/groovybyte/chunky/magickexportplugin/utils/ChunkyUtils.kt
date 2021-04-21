package de.groovybyte.chunky.magickexportplugin.utils

import se.llbit.chunky.renderer.scene.SceneManager
import se.llbit.util.ProgressListener
import se.llbit.util.TaskTracker

val SceneManager.taskTracker: TaskTracker
    get() {
        for (field in this.javaClass.declaredFields) {
            if (field.isOfReadableType<TaskTracker>()) {
                return field.getSafe(this)
            } else if (field.isOfReadableType<SceneManager>()) {
                return field.getSafe<SceneManager>(this).taskTracker
            }
        }
        // no task tracker instance found
        return TaskTracker(ProgressListener.NONE)
    }
