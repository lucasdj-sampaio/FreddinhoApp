package com.fenix.freddinho

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric
import java.io.Serializable

/**
 * Created by Vidyasagar Machupalli on 17/11/16.
 */
class Message : Serializable {
    var id: String? = null
    var message: String? = null
    var url: String? = null
    var title: String? = null
    var description: String? = null
    var type: Type

    constructor() {
        type = Type.TEXT
    }

    constructor(r: RuntimeResponseGeneric) {
        message = ""
        title = r.title()
        description = r.description()
        url = r.source()
        id = "2"
        type = Type.IMAGE
    }

    enum class Type {
        TEXT, IMAGE
    }
}