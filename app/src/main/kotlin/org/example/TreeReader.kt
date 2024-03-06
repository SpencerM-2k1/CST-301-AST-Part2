package org.example

import java.util.ArrayDeque

//Wrapper class to pass int by reference
//class IntRef(var num: Int)

class ParentData(index: Int, depth: Int)
{
    val _index = index
    val _depth = depth
}

class TreeReader()
{
    var _treeLines = ArrayList<String>()
    var _nodeList = ArrayList<Node>()
    var _index = 0;

    var _parentStack = ArrayDeque<ParentData>()
    
    
    constructor(treeLines: ArrayList<String>) : this()
    {
        _treeLines = treeLines
    }

    public fun read() : Node
    {
        _index = 0

        var returnNode = readLine()
        _nodeList.add(returnNode)
        _parentStack.push(ParentData(0, 0))
        //println("Pushed root parent (index: " + (_index - 1) + ", depth: 0)")


        while (_index < _treeLines.size)
        {
            //Workaround: immediately break if empty line is encountered
            if (_treeLines[_index] == "")
                break
            
            var newNode = readLine()

            //Get node w/o parent data
            _nodeList.add(newNode)
            
            if(!_parentStack.isEmpty())
            {
                //Set parent of returnNode
                newNode.setParent(_nodeList[_parentStack.peek()._index])
                //Add returnNode to parent's child list
                _nodeList[_parentStack.peek()._index].addChild(newNode)
            }
            

        } 
        



        return returnNode
    }

    private fun readLine() : Node
    {
        //println("Reading index " + _index)
        
        var prefix = ""
        var type = ""
        var value = ""

        var depth = 0

        //Read Prefix (if not root)
        //println("_index: " + _index)
        if (_index == 0)
        {
            // No node parent
            // Prefix left blank
            depth = 0
        }
        else
        {
            prefix = readPrefix(_treeLines[_index])
            depth = prefix.length / 4 //Prefix length is always a multiple of 4
            if (depth < (_parentStack.peek()._depth + 1))
            {
                //println("   Popped parent (index: " + _parentStack.peek()._index + ", depth: " + _parentStack.peek()._depth + ")")
                //_parentStack.pop()
                popUntilDepth(depth - 1)
            }
            else if (depth > (_parentStack.peek()._depth + 1))
            {
                //println("   Pushed parent (index: " + (_index - 1) + ", depth: " + (depth - 1) + ")")
                _parentStack.push(ParentData(_index - 1, depth - 1))
            }
            //println("   Current parent is now (index: " + (_parentStack.peek()._index) + ", depth: " + (_parentStack.peek()._depth) + ")")
                
        }
        
        val typeStart = prefix.length
        //println("prefix: " + prefix)
        //println("typeStart: " + typeStart)

        //Read Type
        type = readType(_treeLines[_index], typeStart)
        val valueStart = (prefix.length + type.length + 1)
        //println("type: " + type)
        //println("valueStart: " + valueStart)

        
        //Read Value
        //Check if value exists (check length of string vs valueStart)
        if (valueStart > _treeLines[_index].length)
        {
            //  If value does not exist, skip
            //println("value: (none)")
        }
        //  Otherwise, check for value
        else
        {
            value = readValue(_treeLines[_index], valueStart)
            //println("value: " + value)
        }
        var returnNode = Node()
        returnNode.setType(type)
        returnNode.setValue(value)
        returnNode._depth = depth

        _index++
        return returnNode
    }

    private fun readPrefix(nodeString: String) : String
    {
        var returnString = ""
        var i = 0

        while (true)
        {
            returnString += nodeString[i]
            //println(nodeString[i])
            if ((nodeString[i] == '└') || (nodeString[i] == '├'))
            {
                returnString += "── "
                break
            }
            i++
        }

        return returnString
    }

    private fun readType(nodeString: String, startIndex: Int) : String
    {
        var returnString = ""
        var i = startIndex

        while (true)
        {
            if ((i >= nodeString.length) || (nodeString[i] == '\''))
            {
                break
            }
            returnString += nodeString[i]
            i++
        }

        return returnString
    }

    private fun readValue(nodeString: String, startIndex: Int) : String
    {
        var returnString = ""
        var i = startIndex

        while (true)
        {
            if (nodeString[i] == '\'')
            {
                break
            }
            returnString += nodeString[i]
            i++
        }

        return returnString
    }

    //Stack management
    private fun popUntilDepth(targetDepth: Int)
    {
        while (_parentStack.peek()._depth != targetDepth)
        {
            _parentStack.pop()
        }
    }

    //Getters
    public fun getNodeList() : ArrayList<Node> {return _nodeList}

    //Output
    override fun toString() = printNodeList()

    private fun printNodeList() : String
    {
        var returnString = ""
        for (i in 0.._nodeList.size - 1)
        {
            //println(_nodeList[i])
            //println()
            returnString += _nodeList[i]
            returnString += "\n\n"
        }
        return returnString
    }
}