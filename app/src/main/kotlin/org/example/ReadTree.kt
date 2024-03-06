/*
 * Spencer Meren
 * February 16, 2024
 * CST-301
 */

package org.example

import JavaLexer
import JavaParser 
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

//File I/O
import java.io.FileInputStream
import java.io.FileNotFoundException

import java.nio.file.Paths
import java.nio.file.Path
import java.io.File
import java.io.FileWriter
import java.io.BufferedReader
import java.io.FileReader

//Online Documentation
import java.io.*
import java.net.*
import java.util.logging.Logger

fun main() {
    
    try {
        //Filepaths
        val currentPath = Paths.get(".").normalize().toAbsolutePath();
        val loadPath = Paths.get(currentPath.toString(), "src", "main", "assets", "Tree.txt"); //Change File Name here

        //Load tree file into ArrayList of strings
        println(loadPath.toString())
        val fileLines = fileToLines(loadPath.toString())


        //Print tree
        println("======================================")
        println("          PRINTING TREE FILE          ")
        println("======================================")
        for (i in 0..(fileLines.size - 1)) {
           println(fileLines[i])
        }
        println()

        val treeReader = TreeReader(fileLines)
        
        //Print all nodes in tree, in order of their lines in file
        println("======================================")
        println("         PRINTING TREE NODES          ")
        println("======================================")
        val testNode = treeReader.read()
        println(treeReader)

        //Get all class/func references
        val docFetch = DocFetch()
        
        //Show all class refs
        println("======================================")
        println("         PRINTING CLASS INFO          ")
        println("======================================")
        println()
        val classRefs = docFetch.generateClassRefs(treeReader._nodeList)
        for (i in 0..(classRefs.size - 1)) {
           println(classRefs[i]._identifier + "-->" + classRefs[i]._type)
           docFetch.fetchClassDoc(classRefs[i]._type)

           println()
        }
        
        //Show all func refs
        println("======================================")
        println("        PRINTING FUNCTION INFO        ")
        println("======================================")
        val funcRefs = docFetch.generateFuncRefs(treeReader._nodeList,classRefs)
        for (i in 0..(funcRefs.size - 1)) {
           println(funcRefs[i]._func + "() is a function of " + funcRefs[i]._type)
           docFetch.fetchFuncDoc(funcRefs[i]._type, funcRefs[i]._func)

           println()
        }
    }
    catch(err: FileNotFoundException) {
        println("File not found!")
        println("File location: " + System.getProperty("user.dir") + "/src/main/assets/");
    }
    catch(err: ArrayIndexOutOfBoundsException) {
        err.printStackTrace(System.out)
    }
}

//Provided by javin paul
//  https://www.java67.com/2016/07/how-to-read-text-file-into-arraylist-in-java.html
fun fileToLines(fileName: String): ArrayList<String>
{
    val bufReader = BufferedReader(FileReader(fileName));
    var listOfLines = ArrayList<String>();

    var line = bufReader.readLine();
    while (line != null) {
      listOfLines.add(line);
      line = bufReader.readLine();
    }

    bufReader.close();

    return listOfLines;
}