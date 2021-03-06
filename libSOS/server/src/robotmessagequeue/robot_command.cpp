/*
 * robot_command.h
 *
 *  Created on: Nov 1, 2013
 *      Author: jamie
 */

#include "robot_command.h"
#include <vector>
#include <boost/foreach.hpp>
#include "../libSOS/SOSProtocol.h"
#include <sstream>
#include <LogMacros.h>
#include <iomanip>

std::string string_to_hex(const std::string& input)
{
    static const char* const lut = "0123456789ABCDEF";
    size_t len = input.length();

    std::string output;
    output.reserve(2 * len);
    for (size_t i = 0; i < len; ++i)
    {
        const unsigned char c = input[i];
        output.push_back(lut[c >> 4]);
        output.push_back(lut[c & 15]);
    }
    return output;
}

robot_command::robot_command(in_port_t return_id, unsigned char opcode)
:_return_id(return_id),
_opcode(opcode),
 _params(),
 _extraString()
{

}

robot_command::robot_command(in_port_t return_id, unsigned char opcode, boost::optional<std::vector<param_t> > data)
:_return_id(return_id),
_opcode(opcode),
 _params(data),
 _extraString()
{

}

robot_command::robot_command(in_port_t return_id, unsigned char opcode, boost::optional<std::vector<param_t> > data, boost::optional<std::string> extraString)
:_return_id(return_id),
_opcode(opcode),
 _params(data),
 _extraString(extraString)
{

}

boost::optional<robot_command> robot_command::factory(std::vector<char> const & bytes)
{
	in_port_t								return_id;
	char 									opcode;
	boost::optional<std::vector<param_t> > 	params;
	boost::optional<std::string> 			string;


	return_id = parse_return_id(bytes);

	opcode = parse_opcode(bytes);

	std::cout << "command: " << std::setw(2) << string_to_hex(std::string(bytes.begin(), bytes.end())) << std::endl;

	//returns an empty optional if there isn't a param
	params = parse_params(bytes);

	string = parse_string(bytes);

	return boost::optional<robot_command>(robot_command(return_id, opcode, params, string));

}

#if 0

	std::vector<char> myVec(currentBytePtr, std::find(currentBytePtr, endBytePtr, END_ID));

#endif

in_port_t robot_command::parse_return_id(std::vector<char> const & bytes)
{
	std::vector<char>::const_iterator currentBytePtr = std::find(bytes.cbegin(), bytes.cend(), START_TRANSMISSION);
	if(currentBytePtr == bytes.end())
	{
		// Bail and return empty value.
		std::cerr << "RobotCommand: the header of the command is incorrect\nShould be:" << START_TRANSMISSION << " Was: " << *currentBytePtr << std::endl;
		return 0x0;
	}

	// Have start of command.  Begin parsing.


	++currentBytePtr;
	//should now be the first byte of the ASCII-encoded id

	++currentBytePtr;

	//copy elements from currentBytePtr to END_ID into IDStorage
	std::vector<char>::const_iterator endIDPtr=std::find(currentBytePtr, bytes.cend(), (char)(END_ID));
	std::vector<char> IDStorage(currentBytePtr, (endIDPtr));

	//backstop atoi
	IDStorage.push_back(0x0);

	//should now equal END_ID
	return static_cast<in_port_t>(atoi(IDStorage.data()));
}

char robot_command::parse_opcode(std::vector<char> const & bytes)
{
	std::vector<char>::const_iterator currentBytePtr = std::find(bytes.cbegin(), bytes.cend(), START_OPCODE);

	// Get opcode.
	++currentBytePtr;
	char opcode = *currentBytePtr;
	++currentBytePtr;

	if(*currentBytePtr != END_OPCODE)
	{
		// Bail and return empty value.
		LOG_RECOVERABLE("RobotCommand", " the end of the opcode command is incorrect\nShould be:" << END_OPCODE << " Was: " << *currentBytePtr);
		return 0x0;
	}
	return opcode;
}

boost::optional<std::vector<robot_command::param_t> > robot_command::parse_params(std::vector<char> const & bytes)
{
	// Find start of PARAMS, if any.
	std::vector<char>::const_iterator currentBytePtr = std::find(bytes.cbegin(), bytes.cend(), START_PARAMS);


	// Handle case where START_PARAMS does not occur.
	if(currentBytePtr == bytes.cend())
	{
	    // No params in this message; return empty optional object.
		return boost::optional<std::vector<param_t>>();
	}


	 // Loop until we don't receive the start of a param.
	std::vector<param_t> params;
	while(*currentBytePtr == START_PARAMS)
	{
		//copy elements from currentBytePtr to END_PARAMS into paramBytesStorage
		std::vector<char>::const_iterator endShortPtr = std::find(currentBytePtr, bytes.cend(), (char)(END_PARAMS));
		std::vector<char> paramBytesStorage((currentBytePtr + 1), endShortPtr);


		// Null-terminate.
		paramBytesStorage.push_back(0x0);


		// Convert ASCII to double.
		double param = strtod(paramBytesStorage.data(), NULL);
		params.push_back(param);


		if(errno == ERANGE)
		{
			LOG_UNUSUAL("RobotCommand", "Failed to parse parameter " << (const char*)(paramBytesStorage.data()))
		}


		// Advance to next potential START_PARAMS byte.
		currentBytePtr = endShortPtr + 1;
	}

	return boost::optional<decltype(params)>(params);

}


boost::optional<std::string> robot_command::parse_string(std::vector<char> const & bytes)
{
	std::vector<char>::const_iterator currentBytePtr = std::find(bytes.cbegin(), bytes.cend(), START_STRING);

	boost::optional<std::string> optional_string;

	if(currentBytePtr == bytes.cend())
	{
		return optional_string;

	}

	++currentBytePtr;

	std::vector<char>::const_iterator endStringPtr=std::find(currentBytePtr, bytes.cend(), (char)(END_STRING));
	std::string string(currentBytePtr, endStringPtr);

	optional_string.reset(string);
	return optional_string;

}


std::ostream & operator<<(std::ostream & leftOp, const robot_command rightOp)
{
	leftOp << "robot_command dump: --------------------------------------" << std::endl;

	leftOp << "Return ID: " << std::dec << rightOp._return_id << std::endl;

	leftOp << "Opcode: 0x" << std::hex << ((int)rightOp._opcode) << std::endl;

	if(rightOp._params.is_initialized())
	{
		leftOp << "Data: ";

		for(robot_command::param_t subdata : rightOp._params.get())
		{
			leftOp << std::dec << subdata << " ";
		}
		leftOp << std::endl;
	}
	if(rightOp._extraString.is_initialized())
	{
		leftOp << "Extra String: \"" << rightOp._extraString.get() << "\"" << std::endl;
	}
	leftOp << std::endl;

	return leftOp;

}

