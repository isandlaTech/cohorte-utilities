#!/usr/bin/python
#-- Content-Encoding: UTF-8 --
"""
The spell_dictionary component is a simple dictionary implementation of the Dictionary service. It contains few English words.
:authora: Shadi Abras, Thomas Calmant
:copyright: Copyright 2013, isandlaTech
:license:  Apache Software License 2.0
"""

# iPOPO decorators
from pelix.ipopo.decorators import ComponentFactory, Provides, \
    Validate, Invalidate, Requires, BindField, UnbindField

# Standard library
import logging
import re

_logger = logging.getLogger("spellchecker.spell_checker")

# Name the component factory
@ComponentFactory("spell_checker_factory")
# Provide a Spell Checker service
@Provides("spell_checker_service")
# Consume all Spell Dictionary services available (aggregate them)
@Requires("_dictionaries", "spell_dictionary_service", aggregate=True)
class SpellChecker(object):
    """
    A component that uses spell dictionary services to check the spelling of
    given texts.
    """
    def __init__(self):
        """
        Define class members
        """
        # the spell dictionary service, injected list
        self._dictionaries = []

        # the list of available dictionaries, constructed
        self.languages = {}

        # list of some punctuation marks could be found in the given passage,
        # internal
        self.punctuation_marks = None


    @BindField('_dictionaries')
    def bind_dict(self, field, service, svc_ref):
        """
        Called by iPOPO when a spell dictionary service is bound to this
        component
        """
        _logger.info("New installed dictionary")

        # Extract the dictionary language from its properties
        language = svc_ref.get_property('language')

        # Store the service according to its language
        self.languages[language] = service


    @UnbindField('_dictionaries')
    def unbind_dict(self, field, service, svc_ref):
        """
        Called by iPOPO when a dictionary service has gone away
        """
        # Extract the dictionary language from its properties
        language = svc_ref.get_property('language')

        # Remove it from the computed storage
        # The injected list of services is updated by iPOPO
        del self.languages[language]


    @Validate
    def validate(self, context):
        """
        This spell checker has been validated, i.e. at least one dictionary
        service has been bound.
        """
        _logger.info("SpellChecker validated")
        # Set up internal members
        self.punctuation_marks = set((',',';','.','?','!',':', ' '))


    @Invalidate
    def invalidate(self, context):
        """
        The component has been invalidated
        """
        self.punctuation_marks = None


    def check(self, paragraph, language="EN"):
        """
        Checks the given passage for misspelled words.

        @param paragraph the passage to spell check.
        @param language language of the spell dictionary
        @return An array of misspelled words or null if no words are misspelled.
        @raise KeyError No dictionary for this language
        """
        # list of misspelled words in the given passage
        error_list = []

        # list of words to be checked in the given passage without the punctuation marks
        checked_list = re.split("([!,?.:; ])", paragraph)

        try:
            # Get the dictionary corresponding to the requested language
            dictionary = self.languages[language]

        except KeyError:
            # raise KeyError('Unknown language: {0}'.format(language))
            _logger.error("Unknown language, or dictionary provider for this language is not installed!");
            return None

        # Do the job, calling the found service
        return [word for word in checked_list
                if word not in self.punctuation_marks \
                and not dictionary.check_word(word)]
