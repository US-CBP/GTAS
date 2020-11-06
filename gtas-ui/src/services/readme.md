# serviceWrapper is the set of utils that abstract details about specific endpoints and build the crud operations that
# should be exposed for each. An entity represents each endpoint, If the existing crud ops cover all use cases, you
# should only have to add a new line for each entity your app needs. Endpoints that require more complicated logic
# can be handled with new crud operations. The code is structured to reuse those ops across all entities, so be aware
# that edits will be reflected globally.


# The context param can be a uri string if you don't need to post or put a body, or you need the JSON default type.

# The context param should be an object if you need to specify a different contentType for posts/puts.

# The crud operations allowed are: get, post, put, del.



# To create a new entity for a json endpoint, pass a uri string and the crud operations the entity needs:
export const jsonExample = setOps(exampleJsonUri, get, post, put, del);

# To create an entity with a different endpoint type, you need to pass an object containing a contentType attribute and
# a uri, then the crud ops:
export const imageExample = setOps({uri: exampleImageUri, contentType: BMP}, get, post);
