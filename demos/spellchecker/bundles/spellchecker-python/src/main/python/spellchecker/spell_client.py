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
from pelix.ipopo.decorators import ComponentFactory,Property, Provides, \
    Validate, Invalidate, Requires, Instantiate

# Specification of a command service for the Pelix shell
from pelix.shell import SHELL_COMMAND_SPEC

import os

# Name the component factory
@ComponentFactory("spell_client_factory")
# Consume a single Spell Checker service
@Requires("_spell_checker", "spell_checker_service")

class SpellClient(object):
    """
    A component that provides a shell command (spell.spell), using a
    Spell Checker service.
    """
    def __init__(self):
        """
        Defines class members
        """
        # the spell checker service
        self._spell_checker = None


    @Validate
    def validate(self, context):
        """
        Component validated, just print a trace to visualize the event.
        Between this call and the call to invalidate, the _spell_checker member
        will point to a valid spell checker service.
        """
        os.rename("text_to_check.txt", "text_to_check.py")
        import text_to_check
        passage = text_to_check.passage
        language = text_to_check.language
        self.spell(passage, language)

    @Invalidate
    def invalidate(self, context):
        """
        Component invalidated, just print a trace to visualize the event
        """
        os.rename("text_to_check.py", "text_to_check.txt")

    def spell(self, passage, language):
        """
        Reads words from the standard input and checks for their existence
        from the selected dictionary.

        @param io_handler: A utility object given by the shell to interact with
                           the user.
        """
        # Request the language of the text to the user
        language = language.upper()
        misspelled_words = self._spell_checker.check(passage, language)
        f = open('misspelled.txt','w')
        if not misspelled_words:
            f.write('All words are well spelled !\n')
        else:
            f.write("The misspelled words are: {0}", misspelled_words)
        f.close() 
