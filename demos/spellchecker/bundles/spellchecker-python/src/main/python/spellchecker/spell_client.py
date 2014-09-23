#!/usr/bin/python
#-- Content-Encoding: UTF-8 --
"""
This bundle defines a component that consumes a spell checker.
It provides a shell command service, registering a "spell" command that can be
used in the shell of Pelix.

It uses a dictionary service to check for the proper spelling of a word by check
for its existence in the dictionary.

:authors: Shadi Abras, Thomas Calmant
:copyright: Copyright 2013, isandlaTech
:license:  Apache Software License 2.0
"""

# iPOPO decorators
from pelix.ipopo.decorators import ComponentFactory, Provides, Property, \
    Validate, Invalidate, Requires
from pelix.utilities import to_str

import logging
# Basic HTTP server

try:
    # Python 3
    import urllib.parse as urlparse

except ImportError:
    # Python 2
    import urlparse

_logger = logging.getLogger("spellchecker.spell_client")

# Name the component factory
@ComponentFactory("spell_client_factory")
@Provides(specifications='pelix.http.servlet')
@Property('_path', 'pelix.http.path', "/spellchecker")
# Consume a single Spell Checker service
@Requires("_checker", "spell_checker_service")

class SpellClient(object):
    """
    A component that provides a shell command (spell.spell), using a
    Spell Checker service.
    """
    def __init__(self):
        """
        Defines class members
        """
        self._path = None
        # the spell checker service
        self._checker = None

    def bound_to(self, path, params):
        """
        Servlet bound to a path
        """
        _logger.info('Bound to ' + path)
        return True

    def unbound_from(self, path, params):
        """
        Servlet unbound from a path
        """
        _logger.info('Unbound from ' + path)
        return None


    def do_GET(self, request, response):
        """
        Handle a GET
        """
        query = request.get_path()
        query = query[query.rfind('?')+1:]
        data = urlparse.parse_qs(query)
        paragraph = ""
        language = ""
        result = ""
        try:
            paragraph = data['paragraph'][0]
            language = data['language'][0]     
            language = language.upper()
            misspelled_words = self._checker.check(paragraph, language)
            if misspelled_words is None:
                result = 'Dictionary provider for this language is not installed!'
            else:
                if not misspelled_words:
                    result = 'All words are well spelled !'
                else:
                    result = "The misspelled words are: " + " ".join(misspelled_words)
        except (KeyError, IndexError):            
            result = "Fill the language and paragraph inputs!"

        
        content = """<html>
    <head>
    <title>SpellChecker</title>
    </head>
    <body>
    <h2>Spellchecker Demo</h2>
    <hr/>
    <form action="/spellchecker" method="get" >
    Language: <input type="radio" name="language" value="EN">EN  <input type="radio" name="language" value="FR">FR <input type="radio" name="language" value="CN">CN<br/>
    Paragraph: <input type="text" name="paragraph" size="50"/><br/>
    <input type="submit" value="Check"/>
    </form>

    <hr/>
    <b>{result}</b>
    <hr/>
    <ul  style="color:#CCC">
    <li>Client address: {clt_addr[0]}</li>
    <li>Client port: {clt_addr[1]}</li>
    <li>Host: {host}</li>
    <li>Keys: {keys}</li>
    </ul>
    </body>
    </html>""".format(clt_addr=request.get_client_address(),
                    host=request.get_header('host', 0),
                    keys=request.get_headers().keys(),
                    result=result)
        response.send_content(200, content)


    @Validate
    def validate(self, context):
        """
        Component validated, just print a trace to visualize the event.
        Between this call and the call to invalidate, the _spell_checker member
        will point to a valid spell checker service.
        """
        _logger.info("SpellClient validated")        


    @Invalidate
    def invalidate(self, context):
        """
        Component invalidated, just print a trace to visualize the event
        """
        _logger.info("SpellClient invalidated")
