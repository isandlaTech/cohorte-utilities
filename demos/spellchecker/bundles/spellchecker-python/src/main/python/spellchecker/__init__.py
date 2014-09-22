#!/usr/bin/env python
# -- Content-Encoding: UTF-8 --
"""
Spell Checker Demo Module

This module contains the different components of a very simple application.
This application is composed of three components:
  - A component requiring the dictionary service and providing a spell checking service
  - A component providing the english implementation of the dictionary service
  - A component requiring the spell checking service to check to words of user entred sentences

:author: Bassem Debbabi
:license: Apache License v2
"""

# Documentation strings format
__docformat__ = "restructuredtext en"

# ------------------------------------------------------------------------------
# Service specifications

#SERVICE_DICTIONARY = 'demos.spellchecker.dictionaryService'
#""" Specification of a dictionary service
#    This service has only one method:
#       
#       def check(word)
#    
#    which returns 0 if the given word is present in the dictionary, 
#    or 1 if not found.
#"""