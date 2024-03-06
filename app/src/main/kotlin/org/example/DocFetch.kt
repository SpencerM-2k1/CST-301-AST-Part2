package org.example

import java.io.FileNotFoundException

import java.io.File
import java.nio.file.Paths
import java.nio.file.Path

import java.awt.*;
import java.net.URL
import java.awt.datatransfer.StringSelection


//Used for relating variable names with class types for function documentation
open class ClassRef(val _type: String, val _identifier: String)
{
    open public fun getContainedClass() : ClassRef? {return null}
}
// class ContainerRef(type: String, identifier: String, val _containedClass: ClassRef) : ClassRef(type, identifier)
// {
//     override public fun getContainedClass() : ClassRef? {return _containedClass}
// }

class FuncRef(val _func: String, val _type: String) {}

class DocFetch()
{    
    var _classRefs = ArrayList<ClassRef>()
    
    //Getter/setter
    public fun addTypeRef(type: String, identifier: String) {_classRefs.add(ClassRef(type, identifier))}
    public fun getTypeRef(index: Int) : ClassRef {return _classRefs[index]}

    //Generate Refs from list of nodes
    public fun generateClassRefs(nodes: ArrayList<Node>) : ArrayList<ClassRef>
    {
        val refList = ArrayList<ClassRef>()
        for (i in 0..(nodes.size - 1)) {
            if ((nodes[i]._type == "localVariableDeclarationStatement") || (nodes[i]._type == "fieldDeclaration"))
            {
                //println("Node type: " + nodes[i]._type)
                //println("   Node index: " + i)
                refList.add(nodes[i].getClassRef()!!)
            }
           
        }
        return refList
    }

    public fun generateFuncRefs(nodes: ArrayList<Node>, classRefs: ArrayList<ClassRef>) : ArrayList<FuncRef>
    {
        val refList = ArrayList<FuncRef>()
        for (i in 0..(nodes.size - 1)) {
            if (nodes[i]._type == "statementExpression")
            {
                //println("Node type: " + nodes[i]._type)
                //println("   Node index: " + i)
                val newRef = nodes[i].getFuncRef(classRefs)
                if (newRef != null)
                {
                    refList.add(newRef)
                }
            }
           
        }
        return refList
    }

    //Get the type of an identifier
    public fun identifierToType(identifier: String, classRefs: ArrayList<ClassRef>) : String
    {
        for (i in 0..(classRefs.size - 1))
        {
            if (identifier == classRefs[i]._identifier)
                return classRefs[i]._type
        }
        println("Type not found!")
        return ""
    }

    //Print class info
    public fun fetchClassDoc(classType: String)
    {
        try {
            //Filepaths
            val currentPath = Paths.get(".").normalize().toAbsolutePath();
            val loadPath = Paths.get(currentPath.toString(), "src", "main", "assets", "docs", "api", "allclasses-frame.html") //Change File Name here

            val fileLines = fileToLines(loadPath.toString())

            for (i in 0..(fileLines.size - 1)) {
                //println(fileLines[i])
                if(fileLines[i].contains("/" + classType + ".html"))
                {
                    val linkIndex = fileLines[i].indexOf("href=") + 6
                    val classPathSegment = parseClassPath(fileLines[i], linkIndex)
                    //println(classPathSegment)
                    val classDocPath = Paths.get(currentPath.toString(), "src", "main", "assets", "docs", "api", classPathSegment)
                    //println("   Location of documentation: " + classDocPath)
                    println("======================================")
                    println("           CLASS DESCRIPTION          ")
                    println("======================================")

                    printClassDesc(classDocPath)
                    return
                }
            }
            println("======================================")
            println("      NO CLASS DESCRIPTION FOUND      ")
            println("======================================")
            //println()
        }
        catch(e: Exception) {
            println("File not found!")
            println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
        }
    }

    public fun parseClassPath(line: String, linkIndex: Int) : String
    {
        var returnString = ""
        for (i in linkIndex..(line.length - 1))
        {
            if(line[i] == "\""[0]) //Bad practice I'm sure, but my editor's terrible highlighting for Kotlin chars has forced my hand
                break;
            returnString += line[i]
        }
        return returnString
    }

    //Prints the description of the given class
    public fun printClassDesc(classDocPath: Path)
    {
        //Hack: find the first instance of <div class = "block">. This should be the start of the description of the class.
        try {
            //Filepath
            val fileLines = fileToLines(classDocPath.toString())

            var descStartLine = 0;
            var descEndLine = 0;
            //Find lines containing description (start-end)
            for (startIndex in 0..(fileLines.size - 1)) {
                if(fileLines[startIndex].contains("<div class=\"block\">"))
                {
                    descStartLine = startIndex
                    println("Desc start: " + descStartLine)
                    break
                }
            }
            for (endIndex in descStartLine..(fileLines.size - 1))
            {
                if(fileLines[endIndex].contains("</div>"))
                {
                    descEndLine = endIndex
                    println("Desc End: " + descEndLine)
                    break
                }
            }

            for (i in descStartLine..descEndLine)
            {
                println(fileLines[i])
            }
        }
        catch(e: Exception) {
            println("File not found!")
            println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
        }
    }

    public fun fetchFuncDoc(classType: String, funcName: String)
    {
        try {
            //Filepaths
            val currentPath = Paths.get(".").normalize().toAbsolutePath();
            val loadPath = Paths.get(currentPath.toString(), "src", "main", "assets", "docs", "api", "allclasses-frame.html") //Change File Name here

            val fileLines = fileToLines(loadPath.toString())

            for (i in 0..(fileLines.size - 1)) {
                //println(fileLines[i])
                if(fileLines[i].contains("/" + classType + ".html"))
                {
                    val linkIndex = fileLines[i].indexOf("href=") + 6
                    val classPathSegment = parseClassPath(fileLines[i], linkIndex)
                    //println(classPathSegment)
                    val classDocPath = Paths.get(currentPath.toString(), "src", "main", "assets", "docs", "api", classPathSegment)
                    //println("   Location of documentation: " + classDocPath)
                    println("======================================")
                    println("           CLASS DESCRIPTION          ")
                    println("======================================")

                    printFuncDesc(classDocPath, funcName)
                    return
                }
            }
            println("======================================")
            println("      NO CLASS DESCRIPTION FOUND      ")
            println("======================================")
            //println()
        }
        catch(e: Exception) {
            println("File not found!")
            println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
        }
    }

    public fun printFuncDesc(classDocPath: Path, funcName: String)
    {
        //Hack: find an instance of <pre> which also contains function name
        try {
            //Filepath
            val fileLines = fileToLines(classDocPath.toString())

            var descStartLine = 0;
            var descEndLine = 0;
            //Find lines containing description (start-end)
            for (startIndex in 0..(fileLines.size - 1)) {
                if(fileLines[startIndex].contains(funcName + "(<a href="))
                {
                    descStartLine = startIndex
                    println("Desc start: " + descStartLine)
                    break
                }
            }
            for (endIndex in descStartLine..(fileLines.size - 1))
            {
                if(fileLines[endIndex].contains("</div>"))
                {
                    descEndLine = endIndex
                    println("Desc End: " + descEndLine)
                    break
                }
            }

            for (i in descStartLine..descEndLine)
            {
                println(fileLines[i])
            }
        }
        catch(e: Exception) {
            println("File not found!")
            println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
        }
    }
}

//https://www.baeldung.com/kotlin/read-file
//fun readFileDirectlyAsText(fileName: String): String 
//  = File(fileName).readText(Charsets.UTF_8)