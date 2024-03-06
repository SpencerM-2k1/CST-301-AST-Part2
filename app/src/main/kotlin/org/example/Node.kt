/*
 * Spencer Meren
 * February 16, 2024
 * CST-301
 */

package org.example

class Node constructor() {
    
    var _emptyNode = true
    var _type = String()
    var _value = String()
    var _children = ArrayList<Node>()
    var _id: Int? = null
    var _parent: Node? = null

    //Debug
    var _depth: Int? = null

    companion object{
        var numNodes = 0
    }

    init {
        numNodes++
        _id = numNodes
    }

    //Setters
    public fun setType(type: String) { _type = type }
    public fun setValue(value: String) { _value = value }
    public fun setParent(parentNode: Node) { _parent = parentNode }

    //Getters
    //public fun getName() : String {return _name}
    public fun getID() : Int? {return _id}
    public fun isEmpty() : Boolean {return _emptyNode}
    public fun hasParent() : Boolean
    {
        return (_parent != null)
    }

    //Child management
    public fun addChild(childNode: Node)
    {
        _children.add(childNode)
    }

    //Display this node (NOT recursive)
    override fun toString() = printNode()

    private fun printNode(): String
    {
        var returnString = ""

        returnString += _type + " {\n" 
        returnString += "   ID: " + _id + "\n"
        returnString += "   value: " + _value + "\n"
        returnString += "   depth: " + _depth + "\n"
        if (hasParent())
        {
            returnString += "   parentID: " + _parent?.getID() + "\n"
        }
        if (_children.size > 0)
        {
            returnString += "   children: {\n"
            for (i in 0.._children.size - 1)
            {
                returnString += "       ID: " + _children[i]._id + "\n"
            }
            returnString += "   }\n"
        }
        returnString += "}" 
        //returnString += "   parentNode: " + _parent.getName() + "\n"


        return returnString
    }
    
    //Tree traversal
    //  Uses node types similarly to filepath, picking first available match
    public fun nodeAtPath(vararg types: String) : Node?
    {
        //Set up list of nodes
        val typeList = ArrayList<String>()
        for (t in types)
            typeList.add(t)
        
        return nodeAtPath(typeList, 0)
    }

    public fun nodeAtPath(types: ArrayList<String>, index: Int) : Node?
    {
        if(index >= types.size)
        {
            return this;
        }
        //println("Testing!")
        for (c: Node in _children)
        {
            //println("       Found node with type: " + c._type)
            //println("       Found node with value: " + c._value)
            if(c._type == types[index])
            {
                //types.removeAt(0)
                return c.nodeAtPath(types, index + 1)
            }
        }
        return null
    }

    public fun parentAtPath(vararg types: String) : Node?
    {
        //Set up list of nodes
        val typeList = ArrayList<String>()
        for (t in types)
            typeList.add(t)
        
        return parentAtPath(typeList, 0)
    }

    public fun parentAtPath(types: ArrayList<String>, index: Int) : Node?
    {
        if(index >= types.size)
        {
            return this;
        }
        // println("Testing!")
        // println("   Found parent with type: " + _parent?._type)
        // println("   Desired type: " + types[index])
        if(_parent?._type == types[index])
        {
            //types.removeAt(0)
            return _parent?.parentAtPath(types, index + 1)
        }
        return null
    }

    //Generate a class ref from a node
    //  Only run on localVariableDeclaration and memberDeclaration
    public fun getClassRef() : ClassRef?
    {
        if (_type == "localVariableDeclarationStatement")
        {
            //Get Type
            val refType = nodeAtPath("localVariableDeclaration","typeSpec","classOrInterfaceType","superSuffix")?._value
            //Get identifier
            val refIdentifier = nodeAtPath("localVariableDeclaration","variableDeclarators","variableDeclarator","variableDeclaratorId","superSuffix")?._value

            //TODO: Recursively track all "contained objects"
            return ClassRef(refType!!,refIdentifier!!)
        }
        if (_type == "fieldDeclaration")
        {
            //Get Type
            val refType = nodeAtPath("typeSpec","classOrInterfaceType","superSuffix")?._value
            //Get identifier
            val refIdentifier = nodeAtPath("variableDeclarators","variableDeclarator","variableDeclaratorId","superSuffix")?._value

            //TODO: Recursively track all "contained objects"
            return ClassRef(refType!!,refIdentifier!!)
        }

        return null
    }
    
    //Generate a function ref from a node
    public fun getFuncRef(classRefs: ArrayList<ClassRef>) : FuncRef?
    {
        //Object calls method
        if (_type == "statementExpression")
        {
            //Determine if this structure is that of an object is calling a function
            //  Find node with child annotationMethodRest'.', then find function name
            var functionNameNode = nodeAtPath("expression")
            while (functionNameNode!!.nodeAtPath("annotationMethodRest") == null)
            {
                functionNameNode = functionNameNode.nodeAtPath("expression")

                //No function call found, return null
                if (functionNameNode == null)
                    return null
            }
            functionNameNode = functionNameNode.nodeAtPath("superSuffix")
            val functionName = functionNameNode!!._value
            
            //Get identifier of calling object
            var identifierNode = nodeAtPath("expression")
            while (identifierNode!!.nodeAtPath("expression") != null)
            {
                identifierNode = identifierNode.nodeAtPath("expression")
            }

            identifierNode = identifierNode.nodeAtPath("primary","superSuffix")
            val identifier = identifierNode!!._value

            //Convert Identifier to type
            val docFetch = DocFetch()
            val type = docFetch.identifierToType(identifier, classRefs)
            

            return FuncRef(functionName, type)
        }
        
        return null
    }
}