import sys
import re

from pygments.lexer import RegexLexer, bygroups
from pygments.token import Error, Punctuation, Literal, Token, \
     Text, Comment, Operator, Keyword, Name, String, Number, Generic, \
     Whitespace

class AADLLexer(RegexLexer):
    """
    For `AADL <http://www.aadl.info>`_ source code.
    """

    name = 'AADL'
    aliases = ['aadl']
    filenames = ['*.aadl']
    mimetypes = ['text/x-aadl']

    flags = re.MULTILINE | re.DOTALL

    iden_rex = r'[a-zA-Z_][a-zA-Z0-9_\.]*'
    class_iden_rex = r'(' + iden_rex + r')(::)('+ iden_rex + r')'
    definition_rex = r'(' + iden_rex + r')' +  r'(\s*:\s*)\b'
    
    with_tuple = (r'(with)(\s+)', bygroups(Keyword.Namespace, Text), 'with-list')
    text_tuple = (r'[^\S\n]+', Text)
    terminator_triple = (r'(;)(\s+)', bygroups(Punctuation, Whitespace), '#pop')
    property_type_tuple = (r'(' + iden_rex + r')(\s*)(=>)(\s*)', bygroups(Name.Variable, Whitespace, Operator, Whitespace), 'property-value')
    
    tokens = {
         'package': [
             text_tuple,
             (iden_rex, Name.Declaration, '#pop'),
         ],
        'with-list' : [
            (r'\s*(,)\s*', Punctuation),
            (r'[a-zA-Z_]\w*', Name.Namespace),
            terminator_triple, 
        ],
        'package-declaration' : [
            text_tuple,
            (r'(implementation)', Keyword.Declaration),
            (r'(' + iden_rex + r')(;)?', bygroups(Name.Class, Punctuation), '#pop'),
        ],
        'declaration' : [
            text_tuple,
            (r'(in|out|event|data)', Keyword.Constant),
            (r'(flow|path|port|thread)', Keyword.Type),
            (class_iden_rex, bygroups(Name.Class, Punctuation, Name.Entity)),
            (r'(' + iden_rex + r')(\s+)(->|<-|<->)(\s+)('+ iden_rex + r')', bygroups(Name.Variable, Whitespace, Operator, Whitespace, Name.Variable)),
            (iden_rex, Name.Function),
            terminator_triple, 
        ],
        'property-value' : [
            (r'[0-9]+', Number.Integer),
            (r'[0-9]+\.[0-9]*', Number.Float),
            (r'(applies to)', Keyword.Constant),
            (class_iden_rex, bygroups(Name.Class, Punctuation, Name.Constant)),
            (r'(;)(\s+)', bygroups(Punctuation, Whitespace), '#pop'), #should be pop 2?
        ],
        'property-declaration' : [
            text_tuple,
            #(iden_rex, Name.Class),
            property_type_tuple,
        ],
        'property-set' : [
            with_tuple,
            (r'(' + iden_rex + r')(\s+)(is)(\s+)', bygroups(Name.Class, Whitespace, Keyword.Namespace, Whitespace)),
            (definition_rex + r'(constant)', bygroups(Name.Constant, Punctuation, Keyword.Constant), 'property-declaration'),
            (definition_rex, bygroups(Name.Variable.Global, Punctuation), 'property-declaration'),
            (r'(end)(\s+)(' + iden_rex + r')', bygroups(Keyword.Namespace, Whitespace, Name.Class)),
            terminator_triple,
        ],
        'root': [
            (r'(\n\s*|\t)', Whitespace),
            (r'--.*?$', Comment.Single),
            (r'(package)(\s+)', bygroups(Keyword.Namespace, Text), 'package'),
            (r'(public|private)', Keyword.Namespace),
            with_tuple,
            (r'(process|thread)(\s+)', bygroups(Keyword.Declaration, Text), 'package-declaration'),
            (r'(subcomponents|connections|features|flows|properties)(\s+)', bygroups(Keyword.Namespace, Text)),
            (definition_rex, bygroups(Name.Variable, Punctuation), 'declaration'),
            property_type_tuple,
            (r'(end)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'package-declaration'),
            (r'(property set)(\s+)', bygroups(Keyword.Namespace, Whitespace), 'property-set'),
            #(r'([a-zA-Z_][a-zA-Z0-9_]*)(\s)(:)(\s)(in\ out|in|out)(\s)(event\ data|data|event)(\s)(port)(\s)?([a-zA-Z_][a-zA-Z0-9_]*)?(::)?([a-zA-Z_][a-zA-Z0-9_]*)?(;)', bygroups(Name.Variable, Whitespace, Punctuation, Whitespace, Keyword.Constant, Whitespace, Keyword.Constant, Whitespace, Keyword.Type, Whitespace, Name.Class, Punctuation, Name.Entity, Punctuation)),
#           (r'//.*?$', Comment.Single),
#           (r'/\*.*?\*/', Comment.Multiline),
#           text_tuple,
#           (r'(module|open)(\s+)', bygroups(Keyword.Namespace, Text),
#               'module'),
#            'sig': [ 
#             (r'(extends)\b', Keyword, '#pop'),
#             (iden_rex, Name),
#             text_tuple,
#             (r',', Punctuation),
#             (r'\{', Operator, '#pop'),
#         ],
#         'module': [
#             text_tuple,
#             (iden_rex, Name, '#pop'),
#         ],
#         'fun': [
#             text_tuple,
#             (r'\{', Operator, '#pop'),
#             (iden_rex, Name, '#pop'),
#         ],
#             
#             (r'(sig|enum)(\s+)', bygroups(Keyword.Declaration, Text), 'sig'),
#             (r'(iden|univ|none)\b', Keyword.Constant),
#             (r'(int|Int)\b', Keyword.Type),
#             (r'(this|abstract|extends|set|seq|one|lone|let)\b', Keyword),
#             (r'(all|some|no|sum|disj|when|else)\b', Keyword),
#             (r'(run|check|for|but|exactly|expect|as)\b', Keyword),
#             (r'(and|or|implies|iff|in)\b', Operator.Word),
#             (r'(fun|pred|fact|assert)(\s+)', bygroups(Keyword, Text), 'fun'),
#             (r'!|#|&&|\+\+|<<|>>|>=|<=>|<=|\.|->', Operator),
#             (r'[-+/*%=<>&!^|~\{\}\[\]\(\)\.]', Operator),
#             (iden_rex, Name),
#             (r'[:,]', Punctuation),
#             (r'[0-9]+', Number.Integer),
#             (r'"(\\\\|\\"|[^"])*"', String),
#             (r'\n', Text),
        ]
    }