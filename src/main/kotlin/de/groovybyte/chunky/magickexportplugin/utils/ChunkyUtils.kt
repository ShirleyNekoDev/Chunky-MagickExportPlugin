package de.groovybyte.chunky.magickexportplugin.utils

import se.llbit.chunky.main.Chunky
import se.llbit.chunky.renderer.scene.SceneManager
import se.llbit.util.ProgressListener
import se.llbit.util.TaskTracker

/**
 * @author Maximilian Stiede
 */
fun Chunky.isHeadless(): Boolean =
    try {
        javaClass.getDeclaredField("headless").let {
            it.isAccessible = true
            it.getBoolean(this)
        }
    } catch (e: ReflectiveOperationException) {
        // cannot determine if chunky is launched headless - assume true
        true
    }

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
