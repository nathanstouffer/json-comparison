This repository contains a java program that takes in two files in JSON notation and returns their similarity in the range [0.0, 1.0].

NOTE: The three files gson-2.8.6-javadoc.jar gson-2.8.6-sources.jar and gson-2.8.6.jar are not my work.
They are the product of Google and are used in this project to create JSON objects from strings

A quick explanation of how the algorithm computes similarity now follows (a UML class diagram is provided for clarity in the file class-diagram.pdf).
Via the method similarity, the class JsonContainer takes in another JsonContainer and compares the JsonElements within each object.
This is done recursively. Each step in the recursive structure evaluates the types of passed in elements (whether they are null, primitive, objects or arrays).
Once the type is known, the elements are cast to their respective types. Then the similarity can be more directly computed:

For null types, this returns 1.0 if both are null and 0.0 if only 1 element is null.
For primitive types, this is done by converting to strings and testing string equality.
For object types, the similarity is recursively called on each attribute in the object.
For array types, the similarity is recursively called on each element in the array.

Additionally, each step in the recursive structure has a "weight" that defines how much it affects the similarity of those two
specific elements. The weight is spread evenly over the number of distinct keys in each element (keys are integers for arrays).
That is, null and primitive types each assume the full weight of the element when finding similarity since there is only one value.
Whereas, elements in object and array types contribute to a portion of the similarity score for that element.
However, this is applied at each element, so differences further 'down' in the JsonElement affect the overall similarity less.

An example is shown here with JSON files as follows:

file 1:
{
  "name" : "joe",
  "address" : {
    "number" : 123,
    "street" : "oak st",
    "city" : "Bozeman",
    "state" : "MT"
  }
}

file 2:
{
  "name" : "max",
  "address" : {
    "number" : 123,
    "street" : "oak st",
    "city" : "Billings",
    "state" : "MT"
  }
}

The similarity of these two objects would return as 0.375
This is because, at the first level, the weight for each element (name and address) is 0.5.
Note also, that the names differ (which gives similarity as 0 for the name attribute).
Similarity is then equal to (0.5 * 0) + (0.5 * <similarity of the address objects>)
We must then computes the similarity of the address objects.
The weight for each element in address (number, street, city, and state) is 0.25. Here only the city differs.
So the similarity for address is 0.75, but, when computing the overall similarity, this must be multiplied
by each weight on the way back 'up.'
So total similarity is given as (0.5 * 0.0) + (0.5 * 0.75) = 0.375.

The assumptions made by the algorithm now follow.
There are three primary assumptions.
First, the algorithm assumes that input files are written in correct JSON format. If not, an error will occur in
the Gson jar.
Second, it is assumed that a difference in types means a similarity of 0. This means that an object and an array containing the
same elements will return a similarity of 0 even though they contain the same information.
The final assumption has to do with Arrays. For arrays containing the same elements, similarity will only return as 1.0 only if the order of
those elements are the same. Then, the assumption is that corresponding elements in input arrays have the same order.

The jar file is named JSONComparison.jar and is located in JSONComparison/dist/
It requires two files as command line arguments.
