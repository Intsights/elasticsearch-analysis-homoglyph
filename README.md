# Homoglyph Analysis plugin for Elasticsearch

This plugin adds the Homoglyph token filter, which replaces homoglyphs and various unicode versions of alphanumeric characters with the character itself.
For example, the tokens "héllo" will be replaced with "hello".

In case a character in the token has more than one homoglyphic alphanumeric characters, a token with each of the possibilities will be generated.
For example, the character 'ｌ' which, in some fonts, is homoglyphic to both lower case L and the digit 1, will be mapped to both of them.
The resulting set of tokens will contain all of the permutations of all of the possible homoglyphs.
The token 'heｌｌo' will be replaced with the following tokens:
* hello
* hel1o
* he1lo
* he11o

### Notes
* The filter is not case sensitive, and the resulting tokens will be lowercase. Uppercase letters are mapped to their lowercase equivalents.
* The original token is not preserved. Thus, if no character in the token has alphanumeric homoglyphs, no tokens are produced

### Elasticsearch version compatibility
| Plugin version   | Elasticsearch version |
|------------------|-----------------------|
| 0.1.0            | 7.3.1                 |

## Installation
To install the plugin on an Elasticsearch node make sure you are in the Elasticsearch directory and then run:
```
bin/elasticsearch-plugin install https://github.com/intsights/elasticsearch-analysis-homoglyph/releases/download/v0.1.0/analysis-homoglyph-0.1.0.zip
```

You can verify the plugin has been installed by running:
```
bin/elasticsearch-plugin list
```
and making sure `analysis-plugin` is in the list of installed plugins.
For the plugin to start working you need to restart the node.

### With Docker
You can create a Docker container with the plugin installed. 
Save the following in a Dockerfile:
```
FROM docker.elastic.co/elasticsearch/elasticsearch:7.3.1

RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install https://github.com/intsights/elasticsearch-analysis-homoglyph/releases/download/v0.1.0/analysis-homoglyph-0.1.0.zip
```
Then run:
```
docker build -f /path/to/Dockerfile -t elaticsearch-with-homoglyph-plugin:0.1.0 .
docker run -p 9200:9200  -p 9300:9300  -e "discovery.type=single-node" elaticsearch-with-homoglyph-plugin:0.1.0
```
Depending on your system, you might need to run the Docker with `sudo`

## Examples
```
PUT  /homoglyph_example
{
  "settings"  :  {
    "analysis"  :  {
      "analyzer"  :  {
        "standard_homoglyph"  :  {
          "tokenizer"  :  "standard",
          "filter"  :  ["homoglyph"]
        }
      }
    }
  }
}
```

