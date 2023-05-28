package com.example.schedulemanagement

import java.util.Date

data class TaskList(var title : String,
                    var detail : String,
                    var deadline : Date,
                    var priority : Int,
                    var alarm : Date,
                    var complete : String,
                    var myid : String,
                    var parentid : String)
