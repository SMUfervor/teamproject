package com.example.schedulemanagement

import java.util.Date

data class TaskList(var title : String, var deadline : Date, var priority : Int, var alarm : Date, var checkbox : Boolean)
